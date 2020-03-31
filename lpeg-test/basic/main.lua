local json = require 'cjson'

lpeg   = require 'lpeg'
C      = lpeg.C
Cb     = lpeg.Cb
Cf     = lpeg.Cf
Cg     = lpeg.Cg
Cmt    = lpeg.Cmt
Cs     = lpeg.Cs
Ct     = lpeg.Ct
P      = lpeg.P
R      = lpeg.R
S      = lpeg.S
V      = lpeg.V
locale = lpeg.locale()

local util   = require 'util.util'

function render(moduleName)
  local G = require ("basic/" .. moduleName .. "/mdl").grammar()
  local text = util.readFile("basic/" .. moduleName .. "/data")  
  print(text)
  print("--------------------")
  local result = Ct(G):match(text)
  print("--------------------")
  print(util.toText(result))
end

--render("properties")
--render("csv")
--render("calculator")
--render("template")
--render("loop")
--render("date")
render("logiccal") ; table.insert(process , {"BITCOUNT" , lastKey}) ; print(json.encode(process)) ; print(json.encode(recycle)); 