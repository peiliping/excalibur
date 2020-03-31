local mdl = {}

function selector(colNum , op , colVal)
  print(colNum , op , colVal)
  return colNum .. op .. colVal
end

function between(colNum , op , s , e)
 print(colNum , op , s , e)
 return colNum .. op .. s .. e
end

function operation(left , op , right)
  print(left , op , right)
  return left .. op .. right
end

function Blk(p)
  return p * V "Space"
end

function mdl.grammar()
return P{
    V "Space" * V "Stmt" ;
    Stmt      = Cf(V "Group" * Cg(V "LogicSig" * V "Group")^0 , operation) ,
    Group     = V "Element" + V "Open" * V "Stmt" * V "Close" ,
    Element   = V "Element1" + V "Element2" ,
    Element1  = Cg(Blk(V "ColNum") * Blk(V "EqSignal") * Blk(V "ColVal") / selector) ,
    Element2  = Cg(Blk(V "ColNum") * Blk(V "BtSignal") * Blk(V "ColValNum") * Blk(P ",") * Blk(V "ColValNum") / between) ,
    LogicSig  = Blk(C(S "&|")),

    ColNum    = P "C" * C(R "09"^1) ,
    EqSignal  = C(P "=") ,
    BtSignal  = C(P "~") ,
    ColVal    = C((R "az" + R "AZ" + R "09")^1) ,
    ColValNum = C(R "09"^1) ,

    Open      = Blk(P "(") ,
    Close     = Blk(P ")") ,
    Space     = S(" \n\t")^0 ,
}
end

return mdl