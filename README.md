# LightAndShadow
Light &amp; Shadow is an experimental game type set in a quirky Alice in Wonderland inspired setting

![image1](images/2015-10-11-screenshot.png "Choose your team!")


## About this fork

I worked on this module for [Terasology](https://github.com/MovingBlocks/Terasology) working through Google Summer of Code. You can see blog posts about this project and its process [here](https://dacharya64.postach.io/tag/google-summer-of-code), with the final project wrapup outlining the technical details [here](https://dacharya64.postach.io/post/gsoc-final-project-wrapup). 

This project builds upon the existing Terasology game engine, creating a Minimum Viable Product (MVP) for a Capture the Flag game mode in Terasology's Light and Shadow module.

## The Details

**World Generation:** This module generates a world with bases and flags, as well as a spawn platform for new players to choose their team

![](https://cdn-images.postach.io/897b43bc-53d4-4a88-9448-626272b018e0/48c2ae3d-7e9b-4ec4-8c73-db8fb27aa5bc/de5184e1-2ff7-4910-942f-bad21b69d434.jpg)

**Teams and Attack Mechanics:** The module allows players to choose teams, and makes the team of the player affect who the player can attack and how they can interact with either flag. Attacking the other team's players causes them to drop a held flag.

![](https://cdn-images.postach.io/897b43bc-53d4-4a88-9448-626272b018e0/48c2ae3d-7e9b-4ec4-8c73-db8fb27aa5bc/82634b2c-e0d3-42cb-b270-5da752387ff8.jpg)

**Scoring and UI:** There is a UI displaying the current scores for both teams, and syncing the score across multiplayer

![](https://cdn-images.postach.io/897b43bc-53d4-4a88-9448-626272b018e0/48c2ae3d-7e9b-4ec4-8c73-db8fb27aa5bc/8d69e28e-0d1b-488d-a43d-d853b0d769c0.png)


## Testing and Playing:

The game mode is available to play. Download [Terasology](https://github.com/MovingBlocks/Terasology), fetch the Light and Shadow module, and run the game in singleplayer or multiplayer, selecting the Light & Shadow gameplay template and the Light and Shadow (Simple) world generator.


