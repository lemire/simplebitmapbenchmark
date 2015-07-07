@ECHO OFF

MKDIR bin

javac -sourcepath src -classpath ".;lib/*" -d bin src\bitmapbenchmarks\synth\benchmark.java

java -server -cp "bin;lib/*" bitmapbenchmarks.synth.Benchmark

