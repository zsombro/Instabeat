# Instabeat

This is my university thesis project. It's Android application that allows to user to sketch out short pieces of music and play it 
back using a built-in, customizable synthetiser. *This project is not complete yet, but I'm constantly working on it (well, not like I have
any other choice), please do not create any pull requests right now.* I can't accept pull requests, because that would mean I did not
develop this project on my own. I'm using this repository to back up my work and track my progress.

# Latest changes
- MIDI editing features have been added (add/remove notes, playback)
- PureData is integrated

# To do:
- the PureData synth needs to be expanded, most importantly with an envelope filter and a few effects
- a new activity (*SynthSettingsActivity*) has to be added to allow the users to change the sound
- **multitrack support**

# Known bugs:
- sound becomes distorted when the screen is turned off and then back on while the editor is
active (sometimes this also happens when I press back in the Editor and then reopen a song)
- differentiating between keypresses and scrolling doesn't work too well, it needs to be
worked on
