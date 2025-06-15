# Microsoft Developer Studio Project File - Name="Lemonnier" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Java Virtual Machine Java Project" 0x0809

CFG=Lemonnier - Java Virtual Machine Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "Lemonnier.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "Lemonnier.mak" CFG="Lemonnier - Java Virtual Machine Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "Lemonnier - Java Virtual Machine Release" (based on\
 "Java Virtual Machine Java Project")
!MESSAGE "Lemonnier - Java Virtual Machine Debug" (based on\
 "Java Virtual Machine Java Project")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
JAVA=jvc.exe

!IF  "$(CFG)" == "Lemonnier - Java Virtual Machine Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
# ADD BASE JAVA /O
# ADD JAVA /w0 /O

!ELSEIF  "$(CFG)" == "Lemonnier - Java Virtual Machine Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
# ADD BASE JAVA /g
# ADD JAVA /g
# SUBTRACT JAVA /O

!ENDIF 

# Begin Target

# Name "Lemonnier - Java Virtual Machine Release"
# Name "Lemonnier - Java Virtual Machine Debug"
# Begin Group "cinemat"

# PROP Default_Filter "*.java"
# Begin Source File

SOURCE=.\cinemat\Cinemat.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\CinematApplet.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\CinematAppletFrame.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\CinematFrame.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\GraphicDialog.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\Options.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\OptionsDialog.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\Settings.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\Simulation.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationGraphic.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationInfoDisplay.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationRot.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationRotTransl.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationTable.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationTransl.java
# End Source File
# Begin Source File

SOURCE=.\cinemat\SimulationTranslRot.java
# End Source File
# End Group
# Begin Group "util"

# PROP Default_Filter "*.java"
# Begin Source File

SOURCE=.\util\Algorithms.java
# End Source File
# Begin Source File

SOURCE=.\util\Format.java
# End Source File
# Begin Source File

SOURCE=.\util\Preloader.java
# End Source File
# End Group
# Begin Group "ui"

# PROP Default_Filter "*.java"
# Begin Group "animlabel"

# PROP Default_Filter "*.java"
# Begin Source File

SOURCE=.\ui\animlabel\AnimatedLabel.java
# End Source File
# Begin Source File

SOURCE=.\ui\animlabel\Animation.java
# End Source File
# Begin Source File

SOURCE=.\ui\animlabel\HorizBounce.java
# End Source File
# Begin Source File

SOURCE=.\ui\animlabel\Shadow.java
# End Source File
# Begin Source File

SOURCE=.\ui\animlabel\Token.java
# End Source File
# Begin Source File

SOURCE=.\ui\animlabel\Wave.java
# End Source File
# End Group
# Begin Source File

SOURCE=.\ui\AppletHelpDisplayer.java
# End Source File
# Begin Source File

SOURCE=.\ui\AppletImageLoader.java
# End Source File
# Begin Source File

SOURCE=.\ui\AppletParameters.java
# End Source File
# Begin Source File

SOURCE=.\ui\ApplicationImageLoader.java
# End Source File
# Begin Source File

SOURCE=.\ui\AutoDoubleClickList.java
# End Source File
# Begin Source File

SOURCE=.\ui\AxesDrawer.java
# End Source File
# Begin Source File

SOURCE=.\ui\CoordinateMapper.java
# End Source File
# Begin Source File

SOURCE=.\ui\CursorChanger.java
# End Source File
# Begin Source File

SOURCE=.\ui\Exit.java
# End Source File
# Begin Source File

SOURCE=.\ui\Graphic2Display.java
# End Source File
# Begin Source File

SOURCE=.\ui\GraphicDisplay.java
# End Source File
# Begin Source File

SOURCE=.\ui\HelpDisplayer.java
# End Source File
# Begin Source File

SOURCE=.\ui\ImageButton.java
# End Source File
# Begin Source File

SOURCE=.\ui\ImageButton3D.java
# End Source File
# Begin Source File

SOURCE=.\ui\ImageLabel.java
# End Source File
# Begin Source File

SOURCE=.\ui\ImageLoader.java
# End Source File
# Begin Source File

SOURCE=.\ui\LabeledComponent.java
# End Source File
# Begin Source File

SOURCE=.\ui\MessageBox.java
# End Source File
# Begin Source File

SOURCE=.\ui\NoHelpDisplayer.java
# End Source File
# Begin Source File

SOURCE=.\ui\NoParameters.java
# End Source File
# Begin Source File

SOURCE=.\ui\NumericField.java
# End Source File
# Begin Source File

SOURCE=.\ui\NumericInput.java
# End Source File
# Begin Source File

SOURCE=.\ui\PaintableCanvas.java
# End Source File
# Begin Source File

SOURCE=.\ui\Painter.java
# End Source File
# Begin Source File

SOURCE=.\ui\Parameters.java
# End Source File
# Begin Source File

SOURCE=.\ui\PropConstraint.java
# End Source File
# Begin Source File

SOURCE=.\ui\PropConstraints.java
# End Source File
# Begin Source File

SOURCE=.\ui\PropLayout.java
# End Source File
# Begin Source File

SOURCE=.\ui\SizeConstraint.java
# End Source File
# Begin Source File

SOURCE=.\ui\SpriteCanvas.java
# End Source File
# Begin Source File

SOURCE=.\ui\StatusDisplayer.java
# End Source File
# Begin Source File

SOURCE=.\ui\StringVectorParameters.java
# End Source File
# Begin Source File

SOURCE=.\ui\TabsPanel.java
# End Source File
# Begin Source File

SOURCE=.\ui\Test.java
# End Source File
# Begin Source File

SOURCE=.\ui\TextHelpDisplayer.java
# End Source File
# Begin Source File

SOURCE=.\ui\TextHelpFrame.java
# End Source File
# Begin Source File

SOURCE=.\ui\Toolbar.java
# End Source File
# Begin Source File

SOURCE=.\ui\Tooltip.java
# End Source File
# Begin Source File

SOURCE=.\ui\TooltipButton.java
# End Source File
# Begin Source File

SOURCE=.\ui\TrackedDialog.java
# End Source File
# Begin Source File

SOURCE=.\ui\TrackedFrame.java
# End Source File
# Begin Source File

SOURCE=.\ui\UserInterface.java
# End Source File
# Begin Source File

SOURCE=.\ui\VectorInput.java
# End Source File
# Begin Source File

SOURCE=.\ui\VerticalLayout.java
# End Source File
# Begin Source File

SOURCE=.\ui\WindowsTracker.java
# End Source File
# End Group
# Begin Group "numeric"

# PROP Default_Filter "*.java"
# Begin Source File

SOURCE=.\numeric\Demo.java
# End Source File
# Begin Source File

SOURCE=.\numeric\Demo2.java
# End Source File
# Begin Source File

SOURCE=.\numeric\EuclideanNorm.java
# End Source File
# Begin Source File

SOURCE=.\numeric\Functions.java
# End Source File
# Begin Source File

SOURCE=.\numeric\Gauss.java
# End Source File
# Begin Source File

SOURCE=.\numeric\ODE.java
# End Source File
# Begin Source File

SOURCE=.\numeric\ODEInterpolator.java
# End Source File
# Begin Source File

SOURCE=.\numeric\ODESolver.java
# End Source File
# Begin Source File

SOURCE=.\numeric\RealFunction.java
# End Source File
# Begin Source File

SOURCE=.\numeric\VectorNorm.java
# End Source File
# Begin Source File

SOURCE=.\numeric\ZeroFinder.java
# End Source File
# End Group
# Begin Group "fismod"

# PROP Default_Filter "*.java"
# Begin Source File

SOURCE=.\fismod\ComptonDialog.java
# End Source File
# Begin Source File

SOURCE=.\fismod\FisMod.java
# End Source File
# Begin Source File

SOURCE=.\fismod\FisModApplet.java
# End Source File
# Begin Source File

SOURCE=.\fismod\FisModAppletFrame.java
# End Source File
# Begin Source File

SOURCE=.\fismod\FisModFrame.java
# End Source File
# Begin Source File

SOURCE=.\fismod\SimulationBohr.java
# End Source File
# Begin Source File

SOURCE=.\fismod\SimulationCompton.java
# End Source File
# Begin Source File

SOURCE=.\fismod\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\fismod\SimulationSpettro.java
# End Source File
# Begin Source File

SOURCE=.\fismod\SpettroDialog.java
# End Source File
# End Group
# Begin Group "circuiti"

# PROP Default_Filter "*.java"
# Begin Source File

SOURCE=.\circuiti\Circuiti.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\CircuitiApplet.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\CircuitiFrame.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\Circuito.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\CircuitoDisplay.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\Componente.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\Condensatore.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\CondensatoreDialog.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\CortoCircuito.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\EseguiDialog.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\Generatore.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\GeneratoreCC.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\GeneratoreDialog.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\GeneratoreSin.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\GraficoDialog.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\GraficoDisplay.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\Induttore.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\InduttoreDialog.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\Resistore.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\ResistoreDialog.java
# End Source File
# Begin Source File

SOURCE=.\circuiti\SimulationTable.java
# End Source File
# End Group
# Begin Group "dinam1"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\dinam1\Dinam1.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\Dinam1Applet.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\Dinam1Frame.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\GraphicDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\InitialCircDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\InitialFreeDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\InitialPlaneDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\Options.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\OptionsDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\RelGraphic.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\RelRotDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\RelTransDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\Settings.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\Simulation.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SimulationCirc.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SimulationFree.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SimulationGraphic.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SimulationPlane.java
# End Source File
# Begin Source File

SOURCE=.\dinam1\SimulationTable.java
# End Source File
# End Group
# Begin Group "dinam2"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\dinam2\Dinam2.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\Dinam2Applet.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\Dinam2Frame.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\GraphicDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\InitialElastDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\InitialGravDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\Options.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\OptionsDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\Settings.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\Simulation.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\SimulationElast.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\SimulationGraphic.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\SimulationGrav.java
# End Source File
# Begin Source File

SOURCE=.\dinam2\SimulationTable.java
# End Source File
# End Group
# Begin Group "elemag"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\elemag\Animation.java
# End Source File
# Begin Source File

SOURCE=.\elemag\CampoDialog.java
# End Source File
# Begin Source File

SOURCE=.\elemag\CaricaDialog.java
# End Source File
# Begin Source File

SOURCE=.\elemag\CoppiaDialog.java
# End Source File
# Begin Source File

SOURCE=.\elemag\EleMag.java
# End Source File
# Begin Source File

SOURCE=.\elemag\EleMagApplet.java
# End Source File
# Begin Source File

SOURCE=.\elemag\EleMagFrame.java
# End Source File
# Begin Source File

SOURCE=.\elemag\MotoDialog.java
# End Source File
# Begin Source File

SOURCE=.\elemag\ODEEquipot.java
# End Source File
# Begin Source File

SOURCE=.\elemag\ODELineeDiForza.java
# End Source File
# Begin Source File

SOURCE=.\elemag\PianoDialog.java
# End Source File
# Begin Source File

SOURCE=.\elemag\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\elemag\SimulationTable.java
# End Source File
# End Group
# Begin Group "gas"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\gas\Gas.java
# End Source File
# Begin Source File

SOURCE=.\gas\GasApplet.java
# End Source File
# Begin Source File

SOURCE=.\gas\GasFrame.java
# End Source File
# Begin Source File

SOURCE=.\gas\Settings.java
# End Source File
# Begin Source File

SOURCE=.\gas\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\gas\Simulation.java
# End Source File
# Begin Source File

SOURCE=.\gas\SimulationBrown.java
# End Source File
# Begin Source File

SOURCE=.\gas\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\gas\SimulationNMol.java
# End Source File
# Begin Source File

SOURCE=.\gas\SimulationTable.java
# End Source File
# End Group
# Begin Group "onde"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\onde\Interf.java
# End Source File
# Begin Source File

SOURCE=.\onde\Onde.java
# End Source File
# Begin Source File

SOURCE=.\onde\OndeApplet.java
# End Source File
# Begin Source File

SOURCE=.\onde\OndeFrame.java
# End Source File
# Begin Source File

SOURCE=.\onde\Reflect.java
# End Source File
# Begin Source File

SOURCE=.\onde\ReflectDialog.java
# End Source File
# Begin Source File

SOURCE=.\onde\ReflectSettings.java
# End Source File
# Begin Source File

SOURCE=.\onde\Refract.java
# End Source File
# Begin Source File

SOURCE=.\onde\RefractDialog.java
# End Source File
# Begin Source File

SOURCE=.\onde\RefractSettings.java
# End Source File
# Begin Source File

SOURCE=.\onde\Settings.java
# End Source File
# Begin Source File

SOURCE=.\onde\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\onde\SimulationDisplay.java
# End Source File
# End Group
# Begin Group "oscill"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\oscill\Graphic.java
# End Source File
# Begin Source File

SOURCE=.\oscill\GraphicDialog.java
# End Source File
# Begin Source File

SOURCE=.\oscill\GraphicSettings.java
# End Source File
# Begin Source File

SOURCE=.\oscill\Lissajous.java
# End Source File
# Begin Source File

SOURCE=.\oscill\Oscill.java
# End Source File
# Begin Source File

SOURCE=.\oscill\OscillApplet.java
# End Source File
# Begin Source File

SOURCE=.\oscill\OscillFrame.java
# End Source File
# Begin Source File

SOURCE=.\oscill\Settings.java
# End Source File
# Begin Source File

SOURCE=.\oscill\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\oscill\SimulationDisplay.java
# End Source File
# End Group
# Begin Group "ottica"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\ottica\LastraDisplay.java
# End Source File
# Begin Source File

SOURCE=.\ottica\LenteDisplay.java
# End Source File
# Begin Source File

SOURCE=.\ottica\Ottica.java
# End Source File
# Begin Source File

SOURCE=.\ottica\OtticaApplet.java
# End Source File
# Begin Source File

SOURCE=.\ottica\OtticaFrame.java
# End Source File
# Begin Source File

SOURCE=.\ottica\PrismaDisplay.java
# End Source File
# Begin Source File

SOURCE=.\ottica\PrismaIntersezione.java
# End Source File
# Begin Source File

SOURCE=.\ottica\Settings.java
# End Source File
# Begin Source File

SOURCE=.\ottica\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\ottica\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\ottica\SuperficieDisplay.java
# End Source File
# Begin Source File

SOURCE=.\ottica\SuperficieIntersezione.java
# End Source File
# End Group
# Begin Group "relat"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\relat\GraphicDialog.java
# End Source File
# Begin Source File

SOURCE=.\relat\Options.java
# End Source File
# Begin Source File

SOURCE=.\relat\OptionsDialog.java
# End Source File
# Begin Source File

SOURCE=.\relat\Relat.java
# End Source File
# Begin Source File

SOURCE=.\relat\RelatApplet.java
# End Source File
# Begin Source File

SOURCE=.\relat\RelatFrame.java
# End Source File
# Begin Source File

SOURCE=.\relat\Settings.java
# End Source File
# Begin Source File

SOURCE=.\relat\SettingsDialog.java
# End Source File
# Begin Source File

SOURCE=.\relat\Simulation.java
# End Source File
# Begin Source File

SOURCE=.\relat\SimulationDisplay.java
# End Source File
# Begin Source File

SOURCE=.\relat\SimulationGraphic.java
# End Source File
# Begin Source File

SOURCE=.\relat\SimulationInfoDisplay.java
# End Source File
# Begin Source File

SOURCE=.\relat\SimulationTable.java
# End Source File
# End Group
# Begin Source File

SOURCE=.\creadistribuzione.bat
# End Source File
# Begin Source File

SOURCE=.\demon.java
# End Source File
# Begin Source File

SOURCE=.\filetest.java
# End Source File
# Begin Source File

SOURCE=.\MacExe.java
# End Source File
# Begin Source File

SOURCE=.\Sintassi.txt
# End Source File
# Begin Source File

SOURCE=.\test.html
# End Source File
# Begin Source File

SOURCE=.\Win95Exe.java
# End Source File
# End Target
# End Project
