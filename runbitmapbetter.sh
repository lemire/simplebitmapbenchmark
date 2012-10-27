mkdir -p bin
javac  -sourcepath ./src -classpath .:lib/SparseBitmap-0.0.1-SNAPSHOT.jar:lib/JavaEWAH-0.6.1.jar:lib/compressedbitset-0.1.jar:lib/extendedset_2.2.jar -d bin ./src/bitmapbenchmarks/synth/WhenBitmapsBetter.java
java -server -cp bin:lib/SparseBitmap-0.0.1-SNAPSHOT.jar:lib/JavaEWAH-0.6.1.jar:lib/compressedbitset-0.1.jar:lib/extendedset_2.2.jar bitmapbenchmarks.synth.WhenBitmapsBetter
