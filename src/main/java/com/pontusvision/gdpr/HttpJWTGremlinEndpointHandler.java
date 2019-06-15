package com.pontusvision.gdpr;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
//import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tinkerpop.gremlin.driver.MessageSerializer;
import org.apache.tinkerpop.gremlin.driver.Tokens;
import org.apache.tinkerpop.gremlin.driver.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.driver.message.ResponseStatusCode;
import org.apache.tinkerpop.gremlin.driver.ser.MessageTextSerializer;
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.server.GraphManager;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.handler.HttpGremlinEndpointHandler;
import org.apache.tinkerpop.gremlin.server.util.MetricManager;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.util.function.FunctionUtils;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.tinkerpop.shaded.jackson.databind.JsonNode;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.shaded.jackson.databind.node.ArrayNode;
import org.apache.tinkerpop.shaded.jackson.databind.node.ObjectNode;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codahale.metrics.MetricRegistry.name;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpJWTGremlinEndpointHandler extends HttpGremlinEndpointHandler
{
  private static final Logger logger = LoggerFactory.getLogger(HttpGremlinEndpointHandler.class);
  private static final Logger auditLogger = LoggerFactory.getLogger(GremlinServer.AUDIT_LOGGER_NAME);
  private static final Charset UTF8 = Charset.forName("UTF-8");
  static final Meter errorMeter = MetricManager.INSTANCE.getMeter(name(GremlinServer.class, "errors"));

  private static final String ARGS_BINDINGS_DOT = Tokens.ARGS_BINDINGS + ".";

  /**
   * @deprecated As of release 3.1.0, replaced by {@link #ARGS_ALIASES_DOT}.
   */
  @Deprecated
  private static final String ARGS_REBINDINGS_DOT = Tokens.ARGS_REBINDINGS + ".";
  private static final String ARGS_ALIASES_DOT = Tokens.ARGS_ALIASES + ".";

  private static final Timer evalOpTimer = MetricManager.INSTANCE.getTimer(name(GremlinServer.class, "op", "eval"));

  /**
   * Serializers for the response.
   */
  private final Map<String, MessageSerializer> serializers;

  /**
   * This is just a generic mapper to interpret the JSON of a POSTed request.  It is not used for the serialization
   * of the response.
   */
  private static final ObjectMapper mapper = new ObjectMapper();

  private final GremlinExecutor gremlinExecutor;
  private final GraphManager graphManager;
  private final Settings settings;

  private static final Pattern pattern = Pattern.compile("(.*);q=(.*)");

  public HttpJWTGremlinEndpointHandler(Map<String, MessageSerializer> serializers, GremlinExecutor gremlinExecutor,
                                       GraphManager graphManager, Settings settings)
  {
    super(serializers, gremlinExecutor, graphManager, settings);

    this.serializers = serializers;
    this.gremlinExecutor = gremlinExecutor;
    this.graphManager = graphManager;
    this.settings = settings;


  }

  public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
    if (msg instanceof FullHttpRequest) {
      final FullHttpRequest req = (FullHttpRequest) msg;

      if ("/favicon.ico".equals(req.getUri())) {
        sendError(ctx, NOT_FOUND, "Gremlin Server doesn't have a favicon.ico");
//        ReferenceCountUtil.release(msg);
        return;
      }

      if (is100ContinueExpected(req)) {
        ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
      }

      if (req.getMethod() != GET && req.getMethod() != POST) {
        sendError(ctx, METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED.toString());
//        ReferenceCountUtil.release(msg);
        return;
      }

      final Quartet<String, Map<String, Object>, String, Map<String, String>> requestArguments;
      try {
        requestArguments = getRequestArguments(req);
      } catch (IllegalArgumentException iae) {
        sendError(ctx, BAD_REQUEST, iae.getMessage());
//        ReferenceCountUtil.release(msg);
        return;
      }

      final String acceptString = Optional.ofNullable(req.headers().get("Accept")).orElse("application/json");
      final Pair<String, MessageTextSerializer> serializer = chooseSerializer(acceptString);
      if (null == serializer) {
        sendError(ctx, BAD_REQUEST, String.format("no serializer for requested Accept header: %s", acceptString));
//        ReferenceCountUtil.release(msg);
        return;
      }

      final String origin = req.headers().get(ORIGIN);
      final boolean keepAlive = isKeepAlive(req);

      // not using the req any where below here - assume it is safe to release at this point.
//      ReferenceCountUtil.release(msg);

      try {
        logger.debug("Processing request containing script [{}] and bindings of [{}] on {}",
            requestArguments.getValue0(), requestArguments.getValue1(), Thread.currentThread().getName());
        if (settings.authentication.enableAuditLog) {
          String address = ctx.channel().remoteAddress().toString();
          if (address.startsWith("/") && address.length() > 1) address = address.substring(1);
          auditLogger.info("User with address {} requested: {}", address, requestArguments.getValue0());
        }
        final ChannelPromise promise = ctx.channel().newPromise();
        final AtomicReference<Object> resultHolder = new AtomicReference<>();
        promise.addListener(future -> {
          // if failed then the error was already written back to the client as part of the eval future
          // processing of the exception
          if (future.isSuccess()) {
            logger.debug("Preparing HTTP response for request with script [{}] and bindings of [{}] with result of [{}] on [{}]",
                requestArguments.getValue0(), requestArguments.getValue1(), resultHolder.get(), Thread.currentThread().getName());
            final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, (ByteBuf) resultHolder.get());
            response.headers().set(CONTENT_TYPE, serializer.getValue0());
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            // handle cors business
            if (origin != null) response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, origin);

            if (!keepAlive) {
              ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
              response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
              ctx.writeAndFlush(response);
            }
          }
        });

        final Timer.Context timerContext = evalOpTimer.time();

        final Bindings bindings;
        try {
          bindings = createBindings(requestArguments.getValue1(), requestArguments.getValue3());
          bindings.putAll(this.graphManager.getAsBindings());
        } catch (IllegalStateException iae) {
          sendError(ctx, BAD_REQUEST, iae.getMessage());
//          ReferenceCountUtil.release(msg);
          return;
        }

        // provide a transform function to serialize to message - this will force serialization to occur
        // in the same thread as the eval. after the CompletableFuture is returned from the eval the result
        // is ready to be written as a ByteBuf directly to the response.  nothing should be blocking here.
        final CompletableFuture<Object> evalFuture = gremlinExecutor.eval(requestArguments.getValue0(), requestArguments.getValue2(), bindings,
            FunctionUtils.wrapFunction(o -> {
              // stopping the timer here is roughly equivalent to where the timer would have been stopped for
              // this metric in other contexts.  we just want to measure eval time not serialization time.
              timerContext.stop();

              logger.debug("Transforming result of request with script [{}] and bindings of [{}] with result of [{}] on [{}]",
                  requestArguments.getValue0(), requestArguments.getValue1(), o, Thread.currentThread().getName());
              final ResponseMessage responseMessage = ResponseMessage.build(UUID.randomUUID())
                  .code(ResponseStatusCode.SUCCESS)
                  .result(IteratorUtils.asList(o)).create();

              // http server is sessionless and must handle commit on transactions. the commit occurs
              // before serialization to be consistent with how things work for websocket based
              // communication.  this means that failed serialization does not mean that you won't get
              // a commit to the database
              attemptCommit(requestArguments.getValue3(), graphManager, settings.strictTransactionManagement);

              try {
                return Unpooled
                    .wrappedBuffer(serializer.getValue1().serializeResponseAsString(responseMessage).getBytes(UTF8));
              } catch (Exception ex) {
                logger.warn(String.format("Error during serialization for %s", responseMessage), ex);
                throw ex;
              }
            }));

        evalFuture.exceptionally(t -> {
          if (t instanceof org.apache.hadoop.hbase.security.AccessDeniedException | t.getCause() instanceof org.apache.hadoop.hbase.security.AccessDeniedException )
          {
            sendError(ctx);
          }
          else if (t.getMessage() != null)
            sendError(ctx, INTERNAL_SERVER_ERROR, t.getMessage(), Optional.of(t));
          else
            sendError(ctx, INTERNAL_SERVER_ERROR, String.format("Error encountered evaluating script: %s", requestArguments.getValue0())
                , Optional.of(t));
          promise.setFailure(t);
          return null;
        });

        evalFuture.thenAcceptAsync(r -> {
          // now that the eval/serialization is done in the same thread - complete the promise so we can
          // write back the HTTP response on the same thread as the original request
          resultHolder.set(r);
          promise.setSuccess();
        }, gremlinExecutor.getExecutorService());
      } catch (Exception ex) {
        // tossed to exceptionCaught which delegates to sendError method
        final Throwable t = ExceptionUtils.getRootCause(ex);
        throw new RuntimeException(null == t ? ex : t);
      }
    }
  }
  private Bindings createBindings(final Map<String,Object> bindingMap, final Map<String,String> rebindingMap)  {
    final Bindings bindings = new SimpleBindings();

    // rebind any global bindings to a different variable.
    if (!rebindingMap.isEmpty()) {
      for (Map.Entry<String, String> kv : rebindingMap.entrySet()) {
        boolean found = false;
        final Graph g = this.graphManager.getGraph(kv.getValue());
        if (null != g) {
          bindings.put(kv.getKey(), g);
          found = true;
        }

        if (!found) {
          final TraversalSource ts = this.graphManager.getTraversalSource(kv.getValue());
          if (null != ts) {
            bindings.put(kv.getKey(), ts);
            found = true;
          }
        }

        if (!found) {
          final String error = String.format("Could not rebind [%s] to [%s] as [%s] not in the Graph or TraversalSource global bindings",
              kv.getKey(), kv.getValue(), kv.getValue());
          throw new IllegalStateException(error);
        }
      }
    }

    bindings.putAll(bindingMap);

    return bindings;
  }
  private Pair<String,MessageTextSerializer> chooseSerializer(final String acceptString) {
    final List<Pair<String,Double>> ordered = Stream.of(acceptString.split(",")).map(mediaType -> {
      // parse out each mediaType with its params - keeping it simple and just looking for "quality".  if
      // that value isn't there, default it to 1.0.  not really validating here so users better get their
      // accept headers straight
      final Matcher matcher = pattern.matcher(mediaType);
      return (matcher.matches()) ? Pair.with(matcher.group(1), Double.parseDouble(matcher.group(2))) : Pair.with(mediaType, 1.0);
    }).sorted((o1, o2) -> o2.getValue0().compareTo(o1.getValue0())).collect(Collectors.toList());

    for (Pair<String,Double> p : ordered) {
      // this isn't perfect as it doesn't really account for wildcards.  that level of complexity doesn't seem
      // super useful for gremlin server really.
      final String accept = p.getValue0().equals("*/*") ? "application/json" : p.getValue0();
      if (serializers.containsKey(accept))
        return Pair.with(accept, (MessageTextSerializer) serializers.get(accept));
    }

    return null;
  }
  private static Quartet<String, Map<String, Object>, String, Map<String,String>> getRequestArguments(final FullHttpRequest request) {
    if (request.getMethod() == GET) {
      final QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
      final List<String> gremlinParms = decoder.parameters().get(Tokens.ARGS_GREMLIN);

      if (null == gremlinParms || gremlinParms.size() == 0)
        throw new IllegalArgumentException("no gremlin script supplied");
      final String script = gremlinParms.get(0);
      if (script.isEmpty()) throw new IllegalArgumentException("no gremlin script supplied");

      // query string parameters - take the first instance of a key only - ignore the rest
      final Map<String, Object> bindings = new HashMap<>();
      decoder.parameters().entrySet().stream().filter(kv -> kv.getKey().startsWith(ARGS_BINDINGS_DOT))
          .forEach(kv -> bindings.put(kv.getKey().substring(ARGS_BINDINGS_DOT.length()), kv.getValue().get(0)));

      // don't allow both rebindings and aliases parameters as they are the same thing. aliases were introduced
      // as of 3.1.0 as a replacement for rebindings. this check can be removed when rebindings are completely
      // removed from the protocol
      final boolean hasRebindings = decoder.parameters().entrySet().stream().anyMatch(kv -> kv.getKey().startsWith(ARGS_REBINDINGS_DOT));
      final boolean hasAliases = decoder.parameters().entrySet().stream().anyMatch(kv -> kv.getKey().startsWith(ARGS_ALIASES_DOT));
      if (hasRebindings && hasAliases)
        throw new IllegalArgumentException("prefer use of the 'aliases' parameter over 'rebindings' and do not use both");

      final Map<String, String> aliases = new HashMap<>();
      final String rebindingOrAliasParameter = hasRebindings ? ARGS_REBINDINGS_DOT : ARGS_ALIASES_DOT;
      decoder.parameters().entrySet().stream().filter(kv -> kv.getKey().startsWith(rebindingOrAliasParameter))
          .forEach(kv -> aliases.put(kv.getKey().substring(rebindingOrAliasParameter.length()), kv.getValue().get(0)));

      final List<String> languageParms = decoder.parameters().get(Tokens.ARGS_LANGUAGE);
      final String language = (null == languageParms || languageParms.size() == 0) ? null : languageParms.get(0);

      return Quartet.with(script, bindings, language, aliases);
    } else {
      final JsonNode body;
      try {
        body = mapper.readTree(request.content().toString(CharsetUtil.UTF_8));
      } catch (IOException ioe) {
        throw new IllegalArgumentException("body could not be parsed", ioe);
      }

      final JsonNode scriptNode = body.get(Tokens.ARGS_GREMLIN);
      if (null == scriptNode) throw new IllegalArgumentException("no gremlin script supplied");

      final JsonNode bindingsNode = body.get(Tokens.ARGS_BINDINGS);
      if (bindingsNode != null && !bindingsNode.isObject())
        throw new IllegalArgumentException("bindings must be a Map");

      final Map<String, Object> bindings = new HashMap<>();
      if (bindingsNode != null)
        bindingsNode.fields().forEachRemaining(kv -> bindings.put(kv.getKey(), fromJsonNode(kv.getValue())));

      // don't allow both rebindings and aliases parameters as they are the same thing. aliases were introduced
      // as of 3.1.0 as a replacement for rebindings. this check can be removed when rebindings are completely
      // removed from the protocol
      final boolean hasRebindings = body.has(Tokens.ARGS_REBINDINGS);
      final boolean hasAliases = body.has(Tokens.ARGS_ALIASES);
      if (hasRebindings && hasAliases)
        throw new IllegalArgumentException("prefer use of the 'aliases' parameter over 'rebindings' and do not use both");

      final String rebindingOrAliasParameter = hasRebindings ? Tokens.ARGS_REBINDINGS : Tokens.ARGS_ALIASES;
      final JsonNode aliasesNode = body.get(rebindingOrAliasParameter);
      if (aliasesNode != null && !aliasesNode.isObject())
        throw new IllegalArgumentException("aliases must be a Map");

      final Map<String, String> aliases = new HashMap<>();
      if (aliasesNode != null)
        aliasesNode.fields().forEachRemaining(kv -> aliases.put(kv.getKey(), kv.getValue().asText()));

      final JsonNode languageNode = body.get(Tokens.ARGS_LANGUAGE);
      final String language = null == languageNode ? null : languageNode.asText();

      return Quartet.with(scriptNode.asText(), bindings, language, aliases);
    }
  }
  private static void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status,
                                final String message) {
    sendError(ctx, status, message, Optional.empty());
  }

  private static void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status,
                                final String message, final Optional<Throwable> t) {
    if (t.isPresent())
      logger.warn(String.format("Invalid request - responding with %s and %s", status, message), t.get());
    else
      logger.warn(String.format("Invalid request - responding with %s and %s", status, message));

    errorMeter.mark();
    final ObjectNode node = mapper.createObjectNode();
    node.put("message", message);
    if (t.isPresent()) {
      // "Exception-Class" needs to go away - didn't realize it was named that way during review for some reason.
      // replaced with the same method for exception reporting as is used with websocket/nio protocol
      node.put("Exception-Class", t.get().getClass().getName());
      final ArrayNode exceptionList = node.putArray(Tokens.STATUS_ATTRIBUTE_EXCEPTIONS);
      ExceptionUtils.getThrowableList(t.get()).forEach(throwable -> exceptionList.add(throwable.getClass().getName()));
      node.put(Tokens.STATUS_ATTRIBUTE_STACK_TRACE, ExceptionUtils.getFullStackTrace(t.get()));
    }

    final FullHttpResponse response = new DefaultFullHttpResponse(
        HTTP_1_1, status, Unpooled.copiedBuffer(node.toString(), CharsetUtil.UTF_8));
    response.headers().set(CONTENT_TYPE, "application/json");

    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }
  private void sendError(final ChannelHandlerContext ctx)
  {
    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED)).addListener(ChannelFutureListener.CLOSE);
//    ReferenceCountUtil.release(msg);
  }

  private static void attemptCommit(final Map<String, String> aliases, final GraphManager graphManager, final boolean strict) {
    if (strict)
      graphManager.commit(new HashSet<>(aliases.values()));
    else
      graphManager.commitAll();
  }

}
