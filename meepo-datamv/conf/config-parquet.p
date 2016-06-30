#sourceMode=SIMPLEREADER,SYNCREADER
sourceTableName=test_1
#primaryKeyName=id
sourceColumnsNames=id,col1,col2,col3
#sourceExtraSQL=
#readerStepSize=100
#readersNum=1
#start=[AUTO]
#end=[AUTO]
#endDelay=0

targetMode=PARQUETWRITER
targetTableName=test_1
targetColumnsNames=id,col1,col2,col3
#writerStepSize=100
#writersNum=1

parquetOutputPath=/home/peiliping/dev/logs/

#bufferSize=8192
#pluginClass=meepo.storage.plugin.ParseColumnTypePlugin
