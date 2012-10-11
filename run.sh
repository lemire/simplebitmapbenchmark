mkdir -p bin
javac  -sourcepath ./src -classpath .:lib/JavaEWAH-0.5.6.jar:lib/compressedbitset-0.1.jar:lib/extendedset_2.2.jar -d bin ./src/bitmapbenchmarks/synth/benchmark.java
java -server -cp bin:lib/JavaEWAH-0.5.6.jar:lib/compressedbitset-0.1.jar:lib/extendedset_2.2.jar bitmapbenchmarks.synth.benchmark
