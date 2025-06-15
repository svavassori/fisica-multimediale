rem crea le directory
attrib Distribu 
mkdir Distribu
mkdir Distribu\Win32
mkdir Distribu\Win31
mkdir Distribu\Mac
mkdir ..\Classes
copy  /s /m *.class ..\classes

rem crea un file unico win32 per effettuae il preloading
jexegen /MAIN:Demon /BASE:. /OUT:Distribu/Win32/demon.exe /w /r *.class

rem crea un singoli per le varie simulazioni   
jexegen /MAIN:cinemat.Cinemat /OUT:Distribu/Win32/cinemat.exe /w /r cinemat\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:fismod.FisMod /OUT:Distribu/Win32/fismod.exe /w /r fismod\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:circuiti.Circuiti /OUT:Distribu/Win32/circuiti.exe /w /r circuiti\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:dinam1.Dinam1 /OUT:Distribu/Win32/dinam1.exe /w /r dinam1\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:dinam2.Dinam2 /OUT:Distribu/Win32/dinam2.exe /w /r dinam2\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:elemag.EleMag /OUT:Distribu/Win32/elemag.exe /w /r elemag\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:gas.Gas /OUT:Distribu/Win32/gas.exe /w /r gas\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:onde.Onde /OUT:Distribu/Win32/onde.exe /w /r onde\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:oscill.Oscill /OUT:Distribu/Win32/oscill.exe /w /r oscill\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:ottica.Ottica /OUT:Distribu/Win32/ottica.exe /w /r ottica\*.class numeric\*.class ui\*.class util\*.class
jexegen /MAIN:relat.Relat   /OUT:Distribu/Win32/relat.exe /w /r relat\*.class numeric\*.class ui\*.class util\*.class

rem crea il jar per win 3.1 e copia i file htm
copy cinemat1.htm Distribu\Win31
copy cinemat2.htm Distribu\Win31
copy cinemat3.htm Distribu\Win31
copy fismod1.htm Distribu\Win31
copy fismod2.htm Distribu\Win31
copy fismod3.htm Distribu\Win31
j:\javastuff\jdk1.1.4\bin\jar cvf Distribu\Win31\simul.jar *.class fismod\*.class cinemat\*.class ui\*.class util\*.class ui\animlabel\*.class numeric\*.class

