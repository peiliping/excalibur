sourceMode=PARQUETREADER
sourceTableName=app_entity
primaryKeyName=application_id
#sourceColumnsNames=[AUTO] | id,col1,col2,col3
#sourceExtraSQL=
#readerStepSize=100
#readersNum=1
#start=[AUTO]
#end=[AUTO]
#endDelay=0

targetMode=PARQUETWRITER
targetTableName=app_entity
#targetColumnsNames=[AUTO] | id,col1,col2,col3
#writerStepSize=100
#writersNum=1

#bufferSize=8192
#pluginClass=meepo.storage.plugin.ParseColumnTypePlugin

parquetInputPath=/home/peiliping/dev/log/
parquetOutputPath=/home/peiliping/dev/logs/
