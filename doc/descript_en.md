4. Main Programming Assignment: A physics-based game

4.1. Marking attributes

When designing and building your game and report, take into consideration the following marking criteria:

**Game quality (25%)**

The following features will be considered:

- Game originality.
    - What genre does this game fit into?
    - What classic game is it a descendant of, if any?
    - Note that the game does not have to be original, it can just be a clone of an existing game. It would be good if you could vary the game you are cloning in some small way at least. It would also be good if you could just put a lot of effort into making a very faithful clone, including all those features of the original.
- Gameplay - is it fun to play? Does it work well?
- Is the physics convincing?
- Are there any noticeable bugs?
- Are there any hidden bugs? List these in your report. You can gain marks analyzing these bugs correctly, and stating what would be necessary to fix them, if it goes beyond the time allowance of this project to actually make the fix yourself.
- Any polish on the graphics or sound? Fancy graphics and sound are not strictly necessary features for this particular module, in which we are concentrating on physics, but it is a bonus to include such things.

**Physics Features Implemented and Physics Engine (25%)**

You can use one of the open-source physics engines we studied in the course, i.e. JBox2D or JBullet. Or you can use one you made on your own, i.e. build upon the physics engine and methods we have been developing in lectures and labs.

If you use your own physics engine, then extra credit will be given towards the features you have added yourself, and this means you can put less time into the gameplay and sheer number of physics features made use of. If you use your own physics engine, you should include details of what features you manage to implement into your report, to show off what you have achieved. Possible features you could implement, on top of what has been given to you already in the labs:

- timing correction on the collisions
- improved Euler method working correctly on multiple objects
- stable behaviour of numerical integration, especially regarding Hooke's law.
- Any rigid objects implemented, including bouncing and rotation?
- Any novel barrier types included?
- Any novel shapes included?
- Any novel joint types included?
- Any interesting friction models included?
- Any notorious difficulties from physics-engine implementation tackled at all (e.g. ghost collisions, numerical instability, stacked objects creeping through each other or unnecessarily wasting computing resources)

If you use one of the open-source physics engines, and you should try to show that you've used quite a large variety of the features available in the engine, so for example:

- various physics principles included,
- various ways of constructing shapes,
- variety of joint types,
- features such as a pulleys,
- different collision methods, et cetera.

You should list the features you have used in your report, to show off what you have accomplished. You should state why certain solutions have been chosen over other ones, pointing out the advantages of the choices you have made.

**Code quality: (20%)**

- Clarity to read
    - Variables and functions are clearly named. Commented only when necessary.
    - Code is nicely organised. Well structured.
    - Follows the principle of least surprise
    - Good OO principles. Functional break up of components
- Ease of running code. Everything must run out of the box.
- Make sure the code that you created is clearly indicated as your own creation, and that code other people have created is clearly labelled such. E.g. use the comments at the top of each class to indicate this.
- If you use the lab sessions' wrapper for JBox2D, then you could decide to remove that wrapper layer. Preferably, you could refactor it out of existence, or start again from scratch. That way the JBox2D functions more visible, so you can learn the JBox2D API better for yourself. Also removing the wrapper means you don't need to give credit to the module supervisor for writing it. If you use the JBullet demos you should show clearly which bits you have contributed, e.g. in the comments and/or in the report. Similarly, if you use the CE812 physics engine developed in lectures 1-4, you should show clearly which bits you have contributed.

**Report (30%)**

You must submit a report with your work. This should cover at least the following:

A coversheet, including a title, and your student number.

**The Game Description:**

- Include synopsis of your game, instructions how to play it, and a screenshot of the game showing its key features.
- Describe the genre that the game fits into, as described in the section above. What similarities / differences are there to one or more previous games? Include a screenshot of the most relevant previous game for comparison.

**Technical Issues:**

- Briefly justify your choice of physics engine
- Describe the features your game implements, especially the physics features, highlighting those you consider most important. Preferably each feature you highlight should include a code snippet or two, and maybe a screenshot demonstrating where the feature appears in the game, if that feature has a graphical representation. Justify the reason for including that feature, and what it brings to the game.
- Demonstrate some physics principles used in your game. Can you explain the physics well and convincingly, in your own words? Attempt to put something like this into your report to show-off what you have learnt / used on this project / course.
- Discuss any bugs, and unfinished features, as described in the sections above.
- A game such as this has many parameters to tune. Describe which ones you tuned and how you did that to make the game as playable as possible. In particular list details of DeltaT used, the method of integration, masses of key game objects, force magnitudes, and spring constants.

**Reflection:**

- Write a short appraisal of your achievements. How did the project go - what was easy, what was more difficult than expected? Are there any unresolved problems in your final submission? Are there any parts of the game project that you are especially proud of.

**Apportioning credit:**

- Include an appendix showing which classes in your project submission you wrote yourself, and to what degree. E.g. if you include the lab sessions' Vect2D class, then you should acknowledge that this is one of the classes you did not create. Or if you reuse some library functions you wrote for a different module, you should acknowledge that these were not created from scratch for CE812.
- Include details of any artwork / music you did/did not create yourself. Give credit to the authors and license conditions under which you are using it.
- You don't have to credit anything in bundled jar files that your code references, as these are clearly separate from your own code

**Optional video demo (new 13-April-2021):**

- Optionally (there are no extra marks for this), you may include a link to a video demonstration of your game being played. This could be useful for example if your game is very difficult to play.

**Report length:**

There is no word minimum/limit for the report. As a guideline, try to keep it under 10 pages. It just needs to cover the above features, be sufficiently clear to read, and no more. The main purpose of the report is so that you can make clear to the marker the key features that you want to receive credit for. It is in your own interests to do this as clearly as possible.

Once your report has achieved this, then try to focus most of your time on the game implementation rather than the report.

**Option to create your own physics engine:**

If you decide to create your own physics engine, then this will be taken into consideration, and credit given specifically to it.

Your technical skills in implementing those physics features will be considered, e.g. the quality of the numerical integration, or the method you have used to implement rigid bodies, or multiple interacting particles. Hence if you concentrate on your own physics engine, then you don't have to put as much emphasis on including many game features, or physics features, or fancy graphics, as if you had used JBox2D or JBullet, or the BasicPhysicsEngine without significant enhancements.