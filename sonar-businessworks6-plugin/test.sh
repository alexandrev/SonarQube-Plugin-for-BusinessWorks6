path=$1

ruleDef=`cat $1 |grep @Rule`
key=`cat $1 |grep "String RULE_KEY"|cut -d "\"" -f2`
if [[ ! $key == *"@"* ]];then
	echo $key
	description=`echo $ruleDef|cut -d "\"" -f4`
	echo $description >> src/main/resources/org/sonar/l10n/bw/rules/bw/$key.html
fi
