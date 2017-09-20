if [ -d src ]
then
  echo "Source folder exists. Remove it if you want to download selenium-webdriver gem again"
else
  mkdir -p src/main/resources
  java -Xms1024m -Xmx1024m -jar ../../marathon/build/install/marathon/support/jruby-complete-9.1.5.0.jar -S gem install -i ./src/main/resources selenium-webdriver --no-rdoc --no-ri --version 3.5.2
fi
