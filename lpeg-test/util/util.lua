local util = {}

function util.readFile(path)
  local result = {}
  local file = io.open(path,"r")
  for line in file:lines() do
    table.insert(result , line .. "\n")
  end
  file:close()
  return table.concat(result)
end

function util.toText(x)
  local result = {}
  util.unFold(x , result)
  return table.concat(result)
end

function util.unFold(x , result)
  if type(x) == "table" then
    for _ , v in pairs(x) do
      util.unFold(v , result)
    end
  else
    table.insert(result , x)
  end
end

return util