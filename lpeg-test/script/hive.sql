#for x in 20180518 ,20180519 then
 #for y in 0 ,23 then
  alter table abc add partition (pt = ${x} , ht = ${y})
 #end
#end