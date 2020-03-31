local mdl = {}

local dayago = function(offset) return os.date("%Y%m%d", os.time() - 86400 * offset) end

local VARS = {
	yesterday = dayago(1) ,
}

function eval(v)
	print(v)
	return VARS[v]
end

function mdl.grammar()
return P{
  (V "Text" + V "Var")^1 + -1 ;
  Text      = C((1 - P "${")^1) ,
  VarSignal = P "$" ,
  Open      = P "{" * V "Space" ,
  Close     = V "Space" * P "}" ,
  VarName   = Cg(locale.alpha^1 / eval) ,
  Space     = locale.space^0 ,
  Var       = V "VarSignal" * V "Open" * V "VarName" * V "Close" ,
}
end

return mdl