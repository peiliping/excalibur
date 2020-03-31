local mdl = {}

local tmpCount = 1

lastKey = {}
process = {}
recycle = {}

function selector(colNum , op , colVal)
  print(colNum , op , colVal)
  lastKey = {"b" , colNum .. '-' .. colVal}
  return lastKey
end

function operation(left , op , right)
  print(left , op , right)
  local action = ""
  if op == "&" then 
    action = "AND"
  elseif op == "|" then
    action = "OR"
  end
  local tmpKey = "__TMP-" .. tmpCount
  lastKey = {"t" , tmpKey}
  table.insert(recycle , tmpKey)
  tmpCount = tmpCount + 1
  table.insert(process , {"BITOP" , action , tmpKey , left[1] , left[2] , right[1] , right[2]})
  return {"t" , tmpKey }
end

function Blk(p)
  return p * V "Space"
end

function mdl.grammar()
return P{
    V "Space" * V "Stmt" ;
    Stmt      = Cf(V "Group" * Cg(V "LogicSig" * V "Group")^0 , operation) ,
    Group     = V "Element" + V "Open" * V "Stmt" * V "Close" ,
    Element   = Cg(Blk(V "ColNum") * Blk(V "EqSignal") * Blk(V "ColVal") / selector) ,
    LogicSig  = Blk(C(S "&|")),

    ColNum    = P "C" * C(R "09"^1) ,
    EqSignal  = C(P "=") ,
    ColVal    = C((R "az" + R "AZ" + R "09")^1) ,
    ColValNum = C(R "09"^1) ,

    Open      = Blk(P "(") ,
    Close     = Blk(P ")") ,
    Space     = S(" \n\t")^0 ,
}
end

return mdl