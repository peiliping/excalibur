local util    = require 'util.util'
local grammar = require 'script.grammar'

-- print original text
print("--------------Original Text--------------")
local scriptText = util.readFile("script/hive.sql")
print(scriptText)

-- convert 2 lua
print("--------------Parse Temp Data------------")
local parse2Tree = grammar.parse(scriptText)
print("--------------Lua Script Text------------")
local luaScript  = util.toText(parse2Tree)
print(luaScript)

-- run script
local scriptBlock = loadstring(luaScript)
print("--------------Result------------")
local result = scriptBlock()
print("")
--print(result)