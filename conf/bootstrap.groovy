//action

try {
    String gdprModeEnv = System.getenv("PONTUS_GDPR_MODE");
    if (gdprModeEnv != null && Boolean.parseBoolean(gdprModeEnv)) {
        createIndicesPropsAndLabels();
    }
    loadSchema('/tmp/graphSchema_full.json', '/tmp/graphSchema_ext.json')

} catch (e) {
    e.printStackTrace()
}

