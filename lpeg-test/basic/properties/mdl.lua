local mdl = {}

function mdl.grammar()
return P{
    V "pair" * (V "sep" * V "pair")^0 ;
    pair   = Ct(V "key" * "=" * V "value") ,
    key    = V "space" * C(V "kv"^1) * V "space" ,
    value  = V "space" * C(V "kv"^1) * V "space" ,
    space  = locale.space^0 ,
    sep    = S "\n"^0 ,
    kv     = locale.alpha + locale.alnum + S "_-" ,
}
end

return mdl