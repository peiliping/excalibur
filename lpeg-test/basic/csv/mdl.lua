local mdl = {}

function mdl.grammar()
return P{
    V "row" * (V "sep" * V "row")^0 ;
    row    = Ct(V "record" * (P "," * V "record")^0) ,
    record = C((P(1) - S ",\n")^1) ,
    sep    = (S "\n")^0 ,
}
end

return mdl