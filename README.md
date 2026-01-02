# JavaFX Checkers Game (Шашки)

Professional JavaFX implementation of the classic checkers (draughts) board game with Ukrainian localization, modern UI, and advanced gameplay features.

## Features

### Game Mechanics
- **Full Checkers Rules Implementation** - Complete adherence to international draughts rules
- **Multi-Jump Support** - Chain captures in a single turn with automatic detection
- **King Promotions** - Pieces reaching the opposite end become kings with enhanced movement abilities
- **Flying Kings** - Kings can move multiple squares diagonally (international rules variant)
- **Mandatory Captures** - Automatic enforcement of capture rules
- **Move Validation** - Real-time validation prevents illegal moves

### User Interface
- **Modern Design** - Professional gradient backgrounds with elegant styling
- **Intuitive Controls** - Click-based piece selection and movement
- **Visual Feedback** - Highlighted valid moves and forced captures
- **Smooth Animations** - Polished piece movement and capture effects
- **Responsive Layout** - Clean 8x8 board with distinct light/dark squares

### Game Features
- **Room System** - Create custom game rooms with player names
- **Game Timer** - Track match duration from first move
- **Win Detection** - Automatic game-over detection with victory notifications
- **Score Tracking** - Real-time display of captured pieces
- **Reset Option** - Quick game restart without closing application

### Localization
- **Ukrainian Interface** - Full Ukrainian language support
- **Culturally Adapted** - UI text and prompts in Ukrainian for accessibility

## Technology Stack

- **Java 22** - Latest Java LTS features and performance improvements
- **JavaFX 22.0.1** - Modern UI framework for rich desktop applications
- **Maven** - Dependency management and build automation
- **JUnit 5.10.2** - Unit testing framework

## Prerequisites

- JDK 22 or higher
- Maven 3.6+
- JavaFX SDK 22.0.1 (automatically managed by Maven)

## Installation & Running

### Clone the Repository

```bash
git clone https://github.com/sergiyscherbakov/javafx-games-cipher-suite.git
cd javafx-games-cipher-suite
```

### Build & Run with Maven

```bash
# Compile the project
mvn clean compile

# Run the game
mvn clean javafx:run

# Build executable package
mvn clean package
```

### Running from IDE

1. Import as Maven project
2. Ensure JDK 22 is configured
3. Run `CheckersGame.java` main class

## How to Play

### Starting a Game
1. Launch the application
2. Enter player names in the room setup dialog
3. Click "Почати гру" (Start Game) to begin

### Playing
1. **White pieces** move first (bottom of board)
2. Click a piece to select it
3. Valid moves will be highlighted
4. Click a highlighted square to move
5. Captures are mandatory when available (highlighted in gold)
6. Chain multiple captures in one turn when possible

### Winning
- Capture all opponent pieces, or
- Block all opponent moves (no legal moves available)

### Game Timer
- Timer starts on the first move
- Displays elapsed time in minutes and seconds
- Helps track game duration for competitive play

## Game Rules

### Basic Movement
- Pieces move diagonally forward on dark squares only
- Regular pieces can only move forward
- Kings can move forward and backward

### Capturing
- Jump over opponent pieces diagonally to capture them
- Captured pieces are immediately removed from the board
- Multiple captures (chain jumps) must be completed in one turn
- If a capture is available, it must be taken (mandatory capture rule)

### Promotion to King
- When a piece reaches the opponent's back row, it becomes a king
- Kings are marked visually with a crown or special indicator
- Kings have enhanced movement abilities

### Victory Conditions
1. **Capture Victory**: Capture all opponent pieces
2. **Blockade Victory**: Opponent has no legal moves available

## Project Structure

```
javafx-games-cipher-suite/
├── src/main/java/com/example/course4/
│   ├── CheckersGame.java      # Main game application class
│   └── module-info.java        # Java module descriptor
├── src/main/resources/         # Application resources
├── pom.xml                     # Maven configuration
├── .gitignore                  # Git ignore rules
└── README.md                   # This file
```

## Code Architecture

### Main Components

#### CheckersGame Class
- JavaFX Application entry point
- Board rendering and UI management
- Game state tracking
- Event handling for user interactions

#### Key Features Implementation
- **Board Representation**: 2D array-based board state
- **Piece Tracking**: Efficient piece position management
- **Move Validation**: Rule-based legal move calculation
- **Capture Detection**: Automatic multi-jump path finding
- **Timer System**: JavaFX Timeline-based game clock

### Code Quality
- Clean, maintainable code structure
- Comprehensive inline documentation
- Separation of concerns (UI, logic, state)
- Modular method design for extensibility

## Development

### Compilation Settings
- Source: Java 22
- Target: Java 22
- Encoding: UTF-8

### Styling
- JavaFX CSS for visual theming
- Gradient backgrounds for modern appearance
- Drop-shadow effects for depth perception
- Professional color palette (wheat/brown board, white/red pieces)

## Future Enhancements

Potential features for future development:
- AI opponent with difficulty levels
- Online multiplayer support
- Game replay and move history
- Save/load game state
- Alternative rule variants (American, Russian, etc.)
- Move suggestions for beginners
- Tournament mode with multiple rounds

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

Areas for contribution:
- AI opponent implementation
- Additional rule variants
- Performance optimizations
- Enhanced visual effects
- Accessibility improvements

## License

This project is licensed under the MIT License - free to use and modify for educational and personal purposes.

## Author

**Sergiy Scherbakov**
- GitHub: [@sergiyscherbakov](https://github.com/sergiyscherbakov)
- Email: sergiyscherbakov@ukr.net
- Portfolio: [github.com/sergiyscherbakov](https://github.com/sergiyscherbakov)

## Related Projects

Check out my other JavaFX projects:
- [CipherMaster Pro](https://github.com/sergiyscherbakov/cipher-master-pro) - Professional encryption/decryption application

## 💳 Support This Project

If you find this project helpful, consider supporting its development:

### Donate USDT (Binance Smart Chain):

```
🔥 0xDFD0A23d2FEd7c1ab8A0F9A4a1F8386832B6f95A 🔥
```

Your support helps maintain and improve this project. Thank you!

## Acknowledgments

- Built with JavaFX for cross-platform desktop gaming
- Implements classic checkers rules with modern technology
- Ukrainian localization for enhanced accessibility
- Inspired by traditional board games and modern UI/UX design principles

---

*Professional JavaFX game demonstrating advanced UI development, game logic implementation, and software architecture skills*

**Enjoy playing Checkers! (Грайте у шашки з задоволенням!)**
