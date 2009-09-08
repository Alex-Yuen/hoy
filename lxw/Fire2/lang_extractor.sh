#!/bin/sh


awk '
BEGIN{ pattern = "Lang.get(\""}
{
start = index($0,pattern) + length(pattern)
str_temp = substr($0, start )
print substr(str_temp, 1, index(str_temp, "\"")-1)
}
'

