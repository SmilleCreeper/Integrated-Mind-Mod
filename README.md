# Integrated Mind

A NeoForge mod for Minecraft 1.21.1 that adds a fully functional **neural network** system to the game. Build, train, and run artificial neural networks using redstone-powered blocks. The mod introduces blocks that form the components of a neural network including input layers, hidden neurons, weighted connections, and output nodes, all controllable via redstone. The network computes forward passes and backpropagates errors for gradient-based learning, entirely in-game.

## Items

**Brain Unit** is dropped by Iron Golems and serves as the core ingredient. **Axon Wire** is crafted from Brain Units and creates connections with random weights. **Perfect Wire** is crafted and creates connections with fixed weight 1.0.

The remaining items (Neuron Hub, Sensory Receptor, Glands Sac, Tune Membrane, Timbre Muscle, Heart Pump, Ponder Kidney) are thematic pieces inspired by games like *Atomic Heart*. They exist as placeholder ingredients for sub-mods to define their own recipes and functionality on top of Integrated Mind.

## Blocks

### Output Dendrite

Place on any face of a block. Right-click with **Axon Wire** to store a pending connection with a random weight in range [-1, 1]. Right-click with **Perfect Wire** to store a pending connection with weight 1.0.

### Input Dendrite

Place on any face of a block. Right-click while a pending connection exists to link to the Output Dendrite position (max 32 blocks horizontal range). Visualizes connections as colored lines with **blue** for Perfect Wire (weight 1.0), **green** for Axon Wire (positive weight), and **red** for Axon Wire (negative weight). Computes weighted sum of source values during forward pass and backpropagates errors to update weights during training.

### Neuron Block

A hidden layer node in the network. **Redstone from above** triggers a forward pass that reads from the attached Input Dendrite, stores the computed sum, and outputs a comparator signal. **Redstone from below** triggers a backward pass that computes error and backpropagates through the attached Input Dendrite. Emits a comparator signal proportional to the stored value. Wear a **Smart Bubble** as a helmet and interact to cycle through peer pressure learning rates (0.1, 0.01, 0.001, 0.0001, 0.00001).

### Redstone to Precision

Converts a redstone signal (0 to 15) to a float value (0.0625 to 1.0). Faces horizontally and writes the value to an adjacent Precision Memory or Doctrine Watcher on its front face. Triggered on rising edge from any side except front.

### Precision Memory

Stores a float value persistently and emits a comparator signal proportional to the stored value. The value is displayed as text on the left and right sides via a client-side renderer.

### Doctrine Watcher

Reads a float value from an adjacent Precision Memory or Neuron Block on its back face. On a redstone rising edge (any side except front), propagates that value to all connected Neuron Blocks within 32 blocks by scanning all Input Dendrites linked to its front-facing Output Dendrite.

### Smart Bubble

A decorative block that can be placed in-world or worn as a helmet (right-click to equip). Wearing it enables peer pressure configuration on Neuron Blocks.

## License

This mod is released under **Apache 2.0**. It is not affiliated with, endorsed by, or associated with Mojang Studios or Microsoft.

This project serves educational and entertainment purposes, and aims to contribute to the democratization of artificial intelligence by making neural network concepts tangible and interactive within a game environment. It is also a personal creative outlet in response to the ongoing regulatory drama surrounding Anthropic's Claude and Fable.

The author does not claim any copyright over creations including mods, add-ons, maps, builds, videos, or other works made by other users, players, and enthusiasts using this mod.
