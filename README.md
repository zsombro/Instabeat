# Instabeat

This is my university thesis project. It's an Android application that allows the user to sketch out short pieces of music and play them 
back using a built-in, customizable synthetiser. *This project is not complete yet, but I'm constantly working on it (well, not like I have
any other choice), please do not create any pull requests right now.* I can't accept pull requests, because that would mean I did not
develop this project on my own. I'm using this repository to back up my work and track my progress.

# Latest changes
- editing is now possible on multiple tracks and all the notes will be played back!
- the synth has an envelope filter, but it can't be changed yet
- an activity was added for changing synth settings, it's empty tho'

# To do
- the PureData synth needs to be expanded with a few effects, multiple channels
- a new activity (*SynthSettingsActivity*) has to be added to allow users to change the sound
  - instead of sending the track number, the *EditorActivity* should send a batch of parameters in some sort of class
- sending notes to multiple MIDI channels
  - this will be mostly done on the Pure Data side of things

# Current design considerations
- multitrack support
  - this is the most important feature right now, as the SynthSettings and later improvements to the synth engine's interface will depend on it's presence **DONE! AT LEAST I THINK!**
  - the *EditorView* will need to store multiple stacks of notes and swap the ones being rendered after receiving a *setCurrentChannel(int channel)* call **DONE!**
  - the *SynthSettingsActivity* will need to receive a parameter indicating the current channel
  - Theoretically, there is a way for the *Synth* activity to both read and write the state of the synth from the Pure Data code without a Java interface, but this would provide no way to store these parameters (this is also an effort to decouple this component from the *EditorActivity*). Synth settings need to be modeled into a class and saved into the project file. This will require some changes in the file format, obviously
  - Both the relevant event interfaces and event handlers will need to be changed to provide support for channels **DONE!**
  - the Pure Data code needs to be able to separate notes sent to different channels. The synths need to be further abstracted and parameterized. Details on the specifics of these params are still hazy, not cool!

# Known bugs
- sound becomes distorted when the screen is turned off and then back on while the editor is
active (sometimes this also happens when I press back in the Editor and then reopen a song)
- differentiating between keypresses and scrolling doesn't work too well
- at one point the synth DID support polyphony, but this ended up creating a really strange bug where notes would randomly become distorted in the editor. For now, I removed this feature, but eventually I might return to it. **Core first, shine last!**
