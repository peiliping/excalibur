local json = require 'cjson'
local mdl = {}

local MONTHS = {Jan = 1 , Feb = 2 , Mar = 3 , Apr = 4 , May = 5 , Jun =6 , Jul =7  , Aug =8  , Sep =9 , Oct = 10 , Nov = 11 , Dec =12}

D = {}
function init() 
	D = {year = 1970,month = 1,day = 1,hour = 0,min = 0,sec = 0,msec = 0} 
end

function toTS()
	print(json.encode(D))
	return os.time(D) * 1000 + D.msec
end

function mdl.grammar()
return P{
  (V"NewLine"^0 / "") * (V"Space" / init * Cg(V"STM" / toTS) * V"Space" * (V"NewLine"^1))^0 ,
  
  NewLine        = C(P"\n") ;
  Space          = S" \t"^0 ;
  Sp             = S"/ :-."^0 ;
  Number         = R"09" ;
  Year           = C(V"Number" * V"Number" * V"Number" * V"Number" / function(x) if(x == "") then return end D.year  = tonumber(x) end) ;
  Month          = C(R"01"^-1 * V"Number"^-1                       / function(x) if(x == "") then return end D.month = tonumber(x) end) ;
  MonthE         = C(R"AZ" * R"az" * R"az"                         / function(x) if(x == "") then return end D.month = MONTHS[x]   end) ;
  Day            = C(R"03"^-1 * V"Number"^-1                       / function(x) if(x == "") then return end D.day   = tonumber(x) end) ;
  Hour           = C(R"02"^-1 * V"Number"^-1                       / function(x) if(x == "") then return end D.hour  = tonumber(x) end) ;
  Minute         = C(R"05"^-1 * V"Number"^-1                       / function(x) if(x == "") then return end D.min   = tonumber(x) end) ;
  Second         = C(R"05"^-1 * V"Number"^-1                       / function(x) if(x == "") then return end D.sec   = tonumber(x) end) ;
  MSecond        = C(V"Number"^-3                                  / function(x) if(x == "") then return end D.msec  = tonumber(x) end) ;

  STM            = V"DateSTM" * (V"Sp" * V"TimeSTM")^-1 ;
  DateSTM        = V"Year" * V"Sp" * (V"MonthE" + V"Month") * V"Sp" * V"Day" ;
  TimeSTM        = V"Hour" * V"Sp" * V"Minute" * V"Sp" * V"Second" * V"Sp" * V"MSecond" ;
}
end

return mdl