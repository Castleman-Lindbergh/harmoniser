# harmoniser
Live harmonization of vocals using a microphone and MIDI device.

By Thomas Castleman & Johnny Lindbergh.

#### Approach
1. The pitch (if any) of the input signal from the mic (the vocal track) is detected
2. Several copies of this signal are made, one for each note coming through the MIDI device
3. These signals are each pitch-shifted (using a technique called [Pitch Synchronous Overlap and Add](https://en.wikipedia.org/wiki/PSOLA)) to the corresponding frequency of those MIDI notes
4. All the signals are then averaged back together into one, which is played back.

#### Dependencies
This project depends on both [The Midibus](http://www.smallbutdigital.com/projects/themidibus/) for MIDI interactions and [TarsosDSP](https://github.com/JorenSix/TarsosDSP) for audio I/O and pitch detection.

#### Demo
See it in action [here.](https://youtu.be/4izDn3Xud3k)
