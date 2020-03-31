local mdl = {}

local dayago = function(offset) return os.date("%Y%m%d", os.time() - 86400 * offset) end

local VARS = {
  yesterday = dayago(1) ,
}

local inLoop = false

local LoopVarName = nil
local LoopVarValue = {}
local LoopIndex = 1

function evalVars(v)
  if inLoop and v == LoopVarName then
    return LoopVarValue[LoopIndex]
  end
  return VARS[v] and VARS[v] or ""
end

function startLoop()
  inLoop = true
end

function initLoopVar(name)
  LoopVarName = name
  LoopVarValue = {}
end

function trim(e)
  return (e:gsub("^%s*(.-)%s*$", "%1"))
end

function fillEles(...)
  LoopVarValue = {...}
end

function fillRange(s , e)
  for i = s , e do
    table.insert(LoopVarValue , i)
  end
end

function stopLoop()
  inLoop = false
  LoopVarName = nil
  LoopVarValue = {}
end

function evalLoop(block)
  local result = {}
  for i = 1 , #LoopVarValue do
      LoopIndex = i
      result[i] = table.concat({mdl.grammar():match(block)})
  end
  return result
end

function mdl.grammar()
return P{
  (V "Loop" + V "Block")^1 + -1 ;
  Space0    = locale.space^0 ,
  Space1    = locale.space^1 ,
  Number    = R "09"^1 ,
  Var       = P "$" * P "{" * V "Space0" * Cg(V "VarName" / evalVars) * V "Space0" * P "}" ,
  VarName   = locale.alpha^1 ,
  PlainTxt  = C((1 - V "Var" - V "LoopStart" - V "LoopEnd")^1) ,
  Block     = Cg(V "PlainTxt" + V "Var")^1 ,
  LoopBlock = Cg((1 - V "LoopEnd")^1 / evalLoop) ,
  Loop      = V "LoopStart" * (V "VarName" / initLoopVar) * V "LoopIn" * (V "LoopEles" + V "LoopRange") * V "LoopThen" * V "LoopBlock" * V "LoopEnd" ,
  LoopStart = P "#for" * V "Space1" / startLoop ,
  LoopIn    = V "Space1" * P "in" * V "Space1" ,
  LoopRange = P "[" * V "Space0" * C(V "Number") * V "Space0" * P "-" * V "Space0" * C(V "Number") * V "Space0" * P "]" / fillRange ,
  LoopEles  = V "LoopEle" * (P "," * V "LoopEle")^0 / fillEles ,
  LoopEle   = Cg((1 - S ",[]" - P " then")^1 / trim) ,
  LoopThen  = V "Space1" * P "then" * (1 - P "\n")^0 * P "\n" ,
  LoopEnd   = P "#end" / stopLoop ,
}
end

return mdl