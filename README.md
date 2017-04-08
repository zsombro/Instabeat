# Instabeat

This is my university thesis project. It's an Android application that allows the user to sketch out short pieces of music and play them 
back using a built-in, customizable synthetiser. *This project is not complete yet, but I'm constantly working on it (well, not like I have
any other choice), please do not create any pull requests right now.* I can't accept pull requests, because that would mean I did not
develop this project on my own. I'm using this repository to back up my work and track my progress.

# Latest changes
- synth settings are now saved into the project now, **which makes the app officially feature complete!**
    - of course, this does not mean that the app is perfect, or that the synth could not be improved
    and extended with new features, but every feature that I intended is now functional to at least
    some degree. The road to this point was exhausting, but ultimately rewarding and highly educational.
- synth setttings can be changed for each track separately
- notes are separated by MIDI channels, multiple notes can be played at the same time
- MIDI data is now deleted along with project data
- MIDI editing features have been added (add/remove notes, playback)
- PureData has been integrated

# To do
- the PureData synth could be expended upon
    - let the user pick from multiple waveforms
    - adding together multiple waves would be even better tho
    - add options for low pass/high pass/band pass filtering
    - LFO could be neat, but implementing it could be complicated
    since so many things can be controlled by an LFO in a modern synth
- **multitrack support**
    - multiple channels on GUI ✓
    - synth settings separated per channel ✓
    - Pure Data code supports separating MIDI notes by channel ✓
    - synth settings can be applied to synths separately ✓

# Current design considerations
- multitrack support
  - this is the most important feature right now, as the SynthSettings and later improvements to the synth engine's interface will depend on it's presence
  - the *EditorView* will need to store multiple stacks of notes and swap the ones being rendered after receiving a *setCurrentChannel(int channel)* call
  - the *SynthSettingsActivity* will need to receive a parameter indicating the current channel
  - Theoretically, there is a way for the *Synth* activity to both read and write the state of the synth from the Pure Data code without a Java interface, but this would provide no way to store these parameters (this is also an effort to decouple this component from the *EditorActivity*). Synth settings need to be modeled into a class and saved into the project file. This will require some changes in the file format, obviously
  - Both the relevant event interfaces and event handlers will need to be changed to provide support for channels
  - the Pure Data code needs to be able to separate notes sent to different channels. The synths need to be further abstracted and parameterized. Details on the specifics of these params are still hazy, not cool!

# Known bugs
- sound becomes distorted when the screen is turned off and then back on while the editor is
active
    - sometimes this might happen when reopening a song or when changing synth settings
    - this is related to the fact that PureData is a fucking singleton that holds a shitton of state data and is not disposed of when an activity is destroyed, but might be initialized again when the activity is recreated
    - jesus christ, i mean seriously
- differentiating between keypresses and scrolling doesn't work too well