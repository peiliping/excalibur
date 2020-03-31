local mdl = {}

function eval (v1, op, v2)
  print(v1 , op , v2)
  if (op == "+") then return v1 + v2
  elseif (op == "-") then return v1 - v2
  elseif (op == "*") then return v1 * v2
  elseif (op == "/") then return v1 / v2
  end
end

function mdl.grammar()
return P{
    V "Space" * V "Stmt" ;
    Stmt      = Cf(V "Group" * Cg(V "SecondOp"  * V "Group")^0, eval) ,
    Group     = Cf(V "Element" * Cg(V "FirstOp" * V "Element")^0, eval) ,
    Element   = V "Number" / tonumber + V "Open" * V "Stmt" * V "Close" ,

    Space     = S(" \n\t")^0 ,
    Number    = C(P "-"^-1 * R "09"^1 * (P "." * R "09"^1)^0 ) * V "Space" ,
    Open      = P "(" * V "Space" ,
    Close     = P ")" * V "Space" ,
    SecondOp  = C(S "+-") * V "Space" ,
    FirstOp   = C(S "*/") * V "Space" ,
}
end

return mdl