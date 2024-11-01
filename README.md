
# PZChatGuard

PZChatGuard is a Bukkit/Spigot plugin designed to help server owners manage inappropriate chat messages and maintain a healthy community atmosphere. It offers various punishment modes for offensive language and allows for customizable word filtering.

This is a free version of the about-to-be-released PZChatPro.
No support will be offered for this plugin.
Join Our Discord: https://discord.gg/joinjnmc

## Features

- **Customizable Word Filtering:** Define specific words and phrases to filter, along with custom rules for matching.
- **Punishment Modes:** Choose how to punish players for using inappropriate language:
  - **Command:** Execute a custom command (e.g., kick, ban) when a player violates the rules.
  - **Economy:** Deduct an amount from the player's balance using a compatible economy plugin (like Vault).
  - **Replace:** Replace offensive words with specified alternatives in the chat message.
- **Configurable Settings:** Easily configurable through the plugin's YAML configuration files.

## Setup

### Installation

1. Download the latest version of PZChatGuard.
2. Place the JAR file into the `plugins` folder of your Minecraft server.
3. Restart your server to generate the default configuration files.

### Configuration

- **Configuration File:** Open `plugins/PZChatGuard/config.yml` to customize the following settings:
  - `punishment_mode`: Set to `COMMAND`, `ECONOMY`, or `REPLACE`.
  - `custom_command`: The command executed if the punishment mode is set to `COMMAND`.
  - `economy_cost`: The amount deducted from the player if using the `ECONOMY` mode.

- **Word Rules:** Edit `plugins/PZChatGuard/words.yml` to add words to filter, their replacements, and any custom rules. 

### Example Configuration

**config.yml**
```yaml
punishment_mode: REPLACE
custom_command: kick %player% Inappropriate language.
economy_cost: 100
```

**words.yml**
```yaml
words:
  - word: badword
    customrules:
      - "*badword*"
    replacement: "****"
```

## Performance Estimation

PZChatGuard is designed to be lightweight and efficient. The performance impact primarily depends on the number of words and custom rules defined in `words.yml`. For most server setups, it should have minimal impact on performance. However, testing on your server environment is recommended to assess any specific performance concerns.

## Usage as a Dependency

To use PZChatGuard as a dependency in your own plugin:

1. Add PZChatGuard as a dependency in your `plugin.yml` file:

```yaml
depend: [PZChatGuard]
```

2. Access the plugin instance in your code:

```java
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Plugin pzChatGuard = Bukkit.getPluginManager().getPlugin("PZChatGuard");
        if (pzChatGuard != null && pzChatGuard.isEnabled()) {
            // Your logic when PZChatGuard is available
        }
    }
}
```

## Support

For issues, feature requests, or suggestions, please open an issue on the GitHub repository or contact the developer.

## License

This project is licensed under the MIT License.

