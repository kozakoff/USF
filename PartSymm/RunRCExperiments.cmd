@REM Run all Rosenbrock experiments
mkdir xga\exp\hc_rosenbroc
mkdir xga\exp\fga_rosenbroc
mkdir xga\exp\dga_rosenbroc
mkdir xga\exp\sga_rosenbroc

"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/hc_rosenbroc.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/fga_rosenbroc.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/dga_rosenbroc.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/sga_rosenbroc.params

@REM Run all Rastarigin experiments
mkdir xga\exp\hc_rastarigin
mkdir xga\exp\fga_rastarigin
mkdir xga\exp\dga_rastarigin
mkdir xga\exp\sga_rastarigin

"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/hc_rastarigin.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/fga_rastarigin.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/dga_rastarigin.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/sga_rastarigin.params

@REM Run all Ackley experiments
mkdir xga\exp\hc_ackley
mkdir xga\exp\fga_ackley
mkdir xga\exp\dga_ackley
mkdir xga\exp\sga_ackley

"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/hc_ackley.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/fga_ackley.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/dga_ackley.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/sga_ackley.params

@REM Run all Schwefel experiments
mkdir xga\exp\hc_schwefel
mkdir xga\exp\fga_schwefel
mkdir xga\exp\dga_schwefel
mkdir xga\exp\sga_schwefel

"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/hc_schwefel.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/fga_schwefel.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/dga_schwefel.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/sga_schwefel.params

@REM Run all Sphere experiments
mkdir xga\exp\hc_sphere
mkdir xga\exp\fga_sphere
mkdir xga\exp\dga_sphere
mkdir xga\exp\sga_sphere

"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/hc_sphere.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/fga_sphere.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/dga_sphere.params
"C:\Program Files\Java\jdk-10.0.2\bin\javaw.exe" -Dfile.encoding=Cp1252 -classpath "C:\Users\kozaksj\git\USF\PartSymm;C:\Users\kozaksj\git\USF\PartSymm\lib\ecj.24.jar" xga.Evolve -file xga/sga_sphere.params