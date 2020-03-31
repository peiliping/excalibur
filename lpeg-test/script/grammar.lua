local L      = require 'lpeg'
local C      = lpeg.C
local Cb     = lpeg.Cb
local Cf     = lpeg.Cf
local Cg     = lpeg.Cg
local Cmt    = lpeg.Cmt
local Cs     = lpeg.Cs
local Ct     = lpeg.Ct
local P      = lpeg.P
local R      = lpeg.R
local S      = lpeg.S
local V      = lpeg.V
L.locale(L)

local grammar = {}

function toPrintVar(cc)
 return "io.write(" .. cc .. ");"
end

function toPrintText(cc)
 return "io.write('" .. cc .. "');"
end

function toPrintNewLine(x)
  if x == "\n" then
    return "io.write('\\n');"
  end
  return ""
end

local G = Ct{
  V"Plp" ;
  Plp       = V"Shebang"^-1 * V"Skip" * V"Block" ,
  Shebang   = P"#!" * (P(1) - P"\n")^0 ,
  
  Skip      = (V"Space" + V"Comment")^0 ,
  Space     = L.space^1 ,
  Space0    = L.space^0 ,
  Comment   = P"--" * (P(1) - P"\n")^0 ,
  Block     = (V"Text" + V"Stat" + V"Var")^1 + -1 ,
  Stat      = (V"ForStat") * V"EndStat",
  ForStat   = P" "^0 * Cg(P"#for" / "for" * C(V"Space") * C(V"VarName") * C(V"Space") * Cg(P"in" / "=") * C(V"Space") * (V"ForEles") * V"ThenStat" * (V"Text" + V"Var"+ V"Stat")^0) ,
  ForEles   = V"ForEle" * (C(P",") * V "ForEle")^0 ,
  ForEle    = C((1 - P"," - V"ThenStat")^1) ,
  ThenStat  = V"Space" * Ct(P"then" / " do \n") * (1 - P "\n")^0 * P "\n" ,
  VarName   = L.alpha^1 ,
  EndStat   = Cg(V"Space0" * P"#end"  * V"Space" / "\nend"),
  Text      = C((1 - V"Var" - V"Stat" - V"EndStat" - P"\n")^1) / toPrintText * C(P"\n"^-1 / toPrintNewLine)  ,
  Var       = P"$" * P"{" * V"Space0" * Cg(C(V"VarName") / toPrintVar) * V"Space0" * P"}" ,
}

function grammar.parse(script)
  return G:match(script)
end

return grammar