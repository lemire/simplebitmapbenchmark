@ECHO OFF

MKDIR bin

javac -sourcepath src -classpath ".;lib/*" -d bin src\bitmapbenchmarks\synth\Benchmark.java

java -server -cp "bin;lib/*" bitmapbenchmarks.synth.Benchmark

