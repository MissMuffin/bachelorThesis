#Requirements#

| # | User story    | Functional Requirement   |
|:-:|-----------|------------------------|
| 1 | As a knitter I want to be able to see the knitting pattern schematics on my phone while knitting | Display of knitting pattern that is easily usable while knitting|
| 2 | As a knitter I want to create my own schematics in the app both in a grid format and a row format | Pattern editor for creating and editing patterns |
| 3 | As a knitter I want to transcribe schematics from paper into the app with both grid and row formats | Pattern editor |
| 4 | As a knitter I want to have a list of all the patterns in the app and add and remove patterns from that list | CRUD for patterns and showing list of patterns |
| 5 | As a knitter I want to convert metric units for needle sizes, yarn weight and length to imperial and vice versa | Unit converter in app | 
| 6 | As a knitter I would like to enter a set of written knitting instructions and be able to see each individual isntruction while knitting and jump to the next instrcution with a button press | Editor for written instructions and view of them to be used while knitting with button or voice command |
| 7 | As a knitter I want to use my phone to count the rows I knit | Row counter |
| 8 | As a knitter I would like to be able to look up the explanations and visual instructions for different kinds of stitches while inside the app | Glossary of stitches with explanations and instructions |
| 9 | As a knitter I want to have a way to jump to the row I'm currently on in my knitting pattern and to get back to the default zoom level | Button for resetting the zoom level and to jump to current row |
| 10 | As a knitter I want to be able to take pictures of the finished, knitted products of a pattern | In-app camera and function for adding images from disk |
| 11 | As a knitter I want to be able to see pictures of the knitted products of a pattern | Gallery for knitted products from a pattern |
| 12 | As a knitter I want to have all my knitting projects with their details (pattern, required needle size and yarn, etc.) easily accessable in one app | Knitting project management functions |

__*Functional Requirements*__

>Defines **WHAT** a System should do (behavior, function)

1. show/edit/create/delete functions for pattern schematics
2. show pattern schematic while knitting

__*Non-functional Requirements*__

>Defines **HOW** a system works: data integrity, usability, performance)

1. DB → relational
    4. patterns
2. input screen for
    1. pattern
3. pattern editor
4. screens
    5. patterns list
    8. pattern schematics (grid and row views) for knitting
    9. pattern schematic edit screen

##Paper Prototype: Editor##
![editor views][editor]

##Paper Prototype: Viewer##
![viewer views][viewer]

[editor]: paperPrototype/viewsEditor.jpg
[viewer]: paperPrototype/viewsViewer.jpg