# Instabeat

This is my university thesis project. It's Android application that allows to user to sketch out short pieces of music and play it 
back using a built-in, customizable synthetiser. *This project is not complete yet, but I'm constantly working on it (well, not like I have
any other choice), please do not create any pull requests right now.* I can't accept pull requests, because that would mean I did not
develop this project on my own. I'm using this repository to back up my work and track my progress.

# To do:
- the last thing I did was making event callbacks functional from the editor view. the next task will be to
integrate PureData (preferably on a separate thread) and play back notes that are being pressed
- implement note placement in the midi editor. This is a two-step process: the notes that have been placed
have to be reflected in the editor view, but they also have to be added to the MIDI file itself (when should the app
save progress into the midi file itself? the app doesn't store anything else...man, coding that part will not be fun
at all)

# Known bugs:
- sound becomes distorted when the screen is turned off and then back on while the editor is
active
- differentiating between keypresses and scrolling doesn't work too well, it needs to be
worked on