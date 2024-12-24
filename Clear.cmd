echo "delete Binaries"
for /d /r . %%d in (Binaries) do @if exist "%%d" (rd /s /q "%%d" || echo Error deleting "%%d")
echo "delete Intermediate"
for /d /r . %%d in (Intermediate) do @if exist "%%d" (rd /s /q "%%d" || echo Error deleting "%%d")
echo "delete log"
for /d /r . %%d in (log) do @if exist "%%d" (rd /s /q "%%d" || echo Error deleting "%%d")

set TartgetDir=.idea
@if exist %TartgetDir% (rd /s /q %TartgetDir% || echo Error deleting %TartgetDir%)

set TartgetDir=.vs
@if exist %TartgetDir% (rd /s /q %TartgetDir% || echo Error deleting %TartgetDir%)

set TartgetDir=Build
@if exist %TartgetDir% (rd /s /q %TartgetDir% || echo Error deleting %TartgetDir%)

set TartgetDir=DerivedDataCache
@if exist %TartgetDir% (rd /s /q %TartgetDir% || echo Error deleting %TartgetDir%)

set TartgetDir=Platforms
@if exist %TartgetDir% (rd /s /q %TartgetDir% || echo Error deleting %TartgetDir%)

set TartgetDir=Saved
@if exist %TartgetDir% (rd /s /q %TartgetDir% || echo Error deleting %TartgetDir%)

del .vsconfig
del *.sln

pause