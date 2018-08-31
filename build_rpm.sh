#!/bin/sh
rpm_target_dir=/usr/src/packages
rpm_spec_name=sms-adapter.spec
rpmsource=/usr/src/packages/SOURCES

cur_path=`pwd`
echo 'begin build project'
mvn -s settings.xml clean package -Pproduction,build-tar -Dmaven.test.skip=true
echo 'complete build project'
mv target/*.tar ${rpmsource}
dos2unix ${rpm_spec_name}

echo 'begin build rpm'
rpmbuild -bb ${rpm_spec_name} --define="_topdir ${rpm_target_dir}"
echo 'complete build rpm'
cd ${cur_path}
