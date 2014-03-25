del Result.txt
del 8088.txt
for /f "eol= tokens=1,2 delims= " %%i in (ip.txt) do s syn %%i %%j 8088 /save
for /f "eol=- tokens=1 delims= " %%i in (result.txt) do echo %%i>>s1.txt
for /f "eol=P tokens=1 delims= " %%i in (s1.txt) do echo %%i>>s2.txt
for /f "eol=S tokens=1 delims= " %%i in (s2.txt) do echo %%i:8088>>8088.txt
del s1.txt
del s2.txt
del Result.txt