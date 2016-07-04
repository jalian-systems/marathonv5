if [ -d src ]
then
  echo "Source folder exists. Remove it if you want to download selenium-webdriver gem again"
else
  mkdir -p src/main/resources
  java -Xms1024m -Xmx1024m -jar ../../marathon/build/install/marathon/support/jruby-complete-1.7.11.jar -S gem install -i ./src/main/resources selenium-webdriver --no-rdoc --no-ri --version 2.53.4
fi
