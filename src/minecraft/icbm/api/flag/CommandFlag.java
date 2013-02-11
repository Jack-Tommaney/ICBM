package icbm.api.flag;

import java.util.Iterator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.core.vector.Vector3;

/**
 * Commands used for flags and regions. This can be used for protection for specific mod components
 * that might be dangerous.
 * 
 * @author Calclavia
 * 
 */
public class CommandFlag extends CommandBase
{
	public String commandName = "flag";
	public ModFlagData modFlagData;

	public CommandFlag(ModFlagData modFlagData)
	{
		this.modFlagData = modFlagData;
	}

	@Override
	public String getCommandName()
	{
		return commandName;
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "Flags are region based.  \n" + "/" + getCommandName() + " protectOn" + "\n" + "/" + getCommandName() + " protectOff" + "\n" + "/" + getCommandName() + " list" + "\n" + "/" + getCommandName() + " lag" + "\n" + "/" + getCommandName() + " addRegion <name> <radius> <type:all,block,grenade,missile>" + "\n" + "/" + getCommandName() + " removeRegion <name>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		try
		{
			EntityPlayer entityPlayer = (EntityPlayer) sender;

			// The world data the player is on.
			FlagWorld flagWorld = this.modFlagData.getWorldFlags(entityPlayer.worldObj);

			String commandName = args[0].toLowerCase();

			switch (commandName)
			{
			/**
			 * The list command lists out all regions in this world/region.
			 */
				case "list":
				{
					try
					{
						String regionName = args[1];

						if (flagWorld.getRegion(regionName) != null)
						{
							String msg = "List of flags in region " + regionName + ":\n";

							Iterator<Flag> i = flagWorld.getRegion(regionName).flags.iterator();

							while (i.hasNext())
							{
								Flag flag = i.next();
								msg = msg + " " + flag.name + "=>" + flag.value + ",";
							}

							sender.sendChatToPlayer(msg);
						}
						else
						{
							String msg = "Region does not exist, but here are existing flags in the position you are standing on:\n";

							Iterator<Flag> i = flagWorld.getFlagsInPosition(new Vector3(entityPlayer)).iterator();

							while (i.hasNext())
							{
								Flag flag = i.next();
								msg = msg + " " + flag.name + "=>" + flag.value + ",";
							}

							sender.sendChatToPlayer(msg);

							sender.sendChatToPlayer(msg);
						}

					}
					catch (Exception e)
					{
						String msg = "List of regions in this dimension: ";

						Iterator<FlagRegion> i = flagWorld.regions.iterator();
						while (i.hasNext())
						{
							FlagRegion flagRegion = i.next();
							msg = msg + " " + flagRegion.name + " (" + flagRegion.region.min.x + "," + flagRegion.region.min.z + ")" + ",";
						}

						sender.sendChatToPlayer(msg);
					}
					break;
				}
				case "addregion":
				{
					if (args.length > 2)
					{
						String regionName = args[0];
						int radius = Integer.parseInt(args[1]);

						if (radius > 0)
						{
							if (flagWorld.getRegion(regionName) == null)
							{
								flagWorld.addRegion(regionName, new Vector3(entityPlayer), radius);
							}
							else
							{
								throw new WrongUsageException("Region already exists.");
							}
						}
						else
						{
							throw new WrongUsageException("Radius has to be greater than zero!");
						}
					}
					else
					{
						throw new WrongUsageException("/" + this.getCommandName() + " <name> <radius>");
					}

					break;
				}
				case "removeregion":
				{
					if (args.length > 1)
					{
						String regionName = args[1];

						if (flagWorld.removeRegion(regionName))
						{
							sender.sendChatToPlayer("Region with name " + regionName + " is removed.");
						}
						else
						{
							throw new WrongUsageException("The specified region does not exist.");
						}
					}
					else
					{
						throw new WrongUsageException("Please specify the region name.");
					}
					break;
				}
				case "set":
				{
					if (args.length > 2)
					{
						String regionName = args[0];
						String flagName = args[1];
						FlagRegion flagRegion = flagWorld.getRegion(regionName);

						if (flagRegion != null)
						{
							try
							{
								String flagValue = args[2];

								if (FlagRegistry.flags.contains(flagName))
								{
									flagRegion.setFlag(flagName, flagValue);
									sender.sendChatToPlayer("Flag " + flagName + " has been set to " + flagValue + " in " + regionName + ".");
								}
								else
								{
									String flags = "Flag does not exist. Existing flags:\n";

									for (String registeredFlag : FlagRegistry.flags)
									{
										flags = flags + registeredFlag + ", ";
									}

									throw new WrongUsageException(flags);
								}
							}
							catch (Exception e)
							{
								flagRegion.removeFlag(flagName);
							}
						}
						else
						{
							throw new WrongUsageException("The specified region does not exist.");
						}
					}
					else
					{
						throw new WrongUsageException("/" + this.getCommandName() + " set <regionName> <flagName> <value>");
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "protectOn", "protectOff", "list", "lag" }) : null;
	}

}
