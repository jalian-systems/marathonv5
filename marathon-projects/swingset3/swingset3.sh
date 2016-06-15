PROG=`which "$0"`
DIST=`dirname $PROG`

if [ "$DIST" = "." ]
then
	DIST=`pwd`
fi

java -jar $DIST/SwingSet3.jar $*

