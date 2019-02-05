#!/bin/bash
<<<<<<< HEAD
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
OUT_DIR=$DIR/../pontus-dist/opt/pontus/pontus-nifi/current/lib
if [[ ! -d $OUT_DIR ]]; then
  printf "Failed to run; please ensure that pontus-nifi is built first";
  exit 0;
fi

CURDIR=`pwd`
cd $DIR

git pull; 
mvn -Dmaven.test.skip=true -DskipTests=true  clean install ; 
#scp -P 12222 */*/target/*.nar nifi-pontus-elastic-2.x-processor-bundle/nifi-pontus-elastic-2.x-processor/target/nifi-pontus-elastic-2.x-processor-1.0.jar root@localhost:/opt/pontus;
#scp -P 12222 */*/target/*gremlin*.nar */*/target/*service*.nar root@localhost:/opt/pontus;
#cp  */*/target/*gremlin*.nar */*/target/*service*.nar ../nifi-1.2.0.3.0.1.1-5/lib
cp */*/target/*office*.nar  */*/target/*gremlin*.nar */*/target/*service*.nar $OUT_DIR
cp flow.xml.gz nifi.properties ${OUT_DIR}/../conf

cd $CURDIR

echo cd $OUT_DIR
echo "tar cvf - * | docker cp - d:/opt/pontus/pontus-nifi/current/lib"
echo cd -
=======
git pull
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
VERSION=1.2.0
echo DIR is $DIR
export DISTDIR="$DIR/../pontus-dist/opt/pontus/pontus-graph/pv-gdpr-$VERSION";

CURDIR=`pwd`
cd $DIR
mvn -DskipTests clean install

if [[ ! -d $DISTDIR ]]; then
  mkdir -p $DISTDIR
fi

cd $DISTDIR

rm -rf *


cp -r $DIR/bin $DIR/conf $DISTDIR
mkdir -p $DISTDIR/lib

cp $DIR/target/pontus*.jar $DISTDIR/lib
cp $DIR/log4j.properties $DISTDIR/lib

cd ..

unlink current
ln -s pv-gdpr-$VERSION current

cd current
cp $DIR/datadir.tar.gz-* .

cd $CURDIR

echo docker cp $DISTDIR/lib/pontus-gdpr-graph-${VERSION}.jar d:/opt/pontus/pontus-graph/current/lib


>>>>>>> 510b593c5adb2159d77a18a423aca367fa3d4c5b
