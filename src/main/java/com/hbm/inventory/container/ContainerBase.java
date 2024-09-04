package com.hbm.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * For now, only used for stuff with filters and crates as a reference
 * implementation, because I really needed to get the te from a container But
 * you should very much use this to kill the giant amount of boilerplate in
 * container classes
 * 
 * @author 70k
 **/
public class ContainerBase extends Container {

	public IInventory tile;

	public ContainerBase(InventoryPlayer invPlayer, IInventory tedf) {
		tile = tedf;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotOriginal = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotOriginal = slotStack.copy();

			if(index < tile.getSizeInventory()) {
				if(!this.mergeItemStack(slotStack, tile.getSizeInventory(), this.inventorySlots.size(), true)) {
					return null;
				}
			} else if(!this.mergeItemStack(slotStack, 0, tile.getSizeInventory(), false)) {
				return null;
			}

			if(slotStack.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			slot.onPickupFromSlot(player, slotStack);
		}

		return slotOriginal;
	}

	/** Used to quickly set up the player inventory */
	public void playerInv(InventoryPlayer invPlayer, int playerInvX, int playerInvY, int playerHotbarY) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, playerInvX + j * 18, playerInvY + i * 18));
			}
		}

		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, playerInvX + i * 18, playerHotbarY));
		}
	}

	// I'm gonna make a farken helper function for this shit, why was it done
	// the old way for 9 whole ass years?
	// - Mellow, 1884
	/**
	 * Used to add several conventional inventory slots at a time
	 * 
	 * @param inv the inventory to add the slots to
	 * @param from the slot index to start from
	 */
	public void addSlots(IInventory inv, int from, int x, int y, int rows, int cols) {
		int slotSize = 18;
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				this.addSlotToContainer(new Slot(inv, col + row * cols + from, x + col * slotSize, y + row * slotSize));
			}
		}
	}

	// Fixed mergeItemStack that actually respects stacksize limits AND whether the slot actually accepts the fucking item
	@Override
	protected boolean mergeItemStack(ItemStack stack, int fromInclusive, int toExclusive, boolean toPlayerInventory) {
		boolean didMerge = false;
		int targetIndex = fromInclusive;

		if(toPlayerInventory) {
			targetIndex = toExclusive - 1;
		}

		Slot targetSlot;
		ItemStack targetStack;

		int maxStackSize = stack.getMaxStackSize();
		if(!toPlayerInventory) maxStackSize = Math.min(maxStackSize, tile.getInventoryStackLimit());

		// Stack on top of existing
		if(stack.isStackable()) {
			while(stack.stackSize > 0 && (!toPlayerInventory && targetIndex < toExclusive || toPlayerInventory && targetIndex >= fromInclusive)) {
				targetSlot = getSlot(targetIndex);
				targetStack = targetSlot.getStack();

				if(targetStack != null && targetStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == targetStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, targetStack)) {
					if(targetSlot.isItemValid(stack)) {
						int l = targetStack.stackSize + stack.stackSize;
	
						if(l <= maxStackSize) {
							stack.stackSize = 0;
							targetStack.stackSize = l;
							targetSlot.onSlotChanged();
							didMerge = true;
						} else if(targetStack.stackSize < maxStackSize) {
							stack.stackSize -= maxStackSize - targetStack.stackSize;
							targetStack.stackSize = maxStackSize;
							targetSlot.onSlotChanged();
							didMerge = true;
						}
					}
				}

				if(toPlayerInventory) {
					--targetIndex;
				} else {
					++targetIndex;
				}
			}
		}

		// Remainder into an empty slot
		if(stack.stackSize > 0) {
			if(toPlayerInventory) {
				targetIndex = toExclusive - 1;
			} else {
				targetIndex = fromInclusive;
			}

			while(!toPlayerInventory && targetIndex < toExclusive || toPlayerInventory && targetIndex >= fromInclusive) {
				targetSlot = getSlot(targetIndex);
				targetStack = targetSlot.getStack();

				if(targetStack == null && targetSlot.isItemValid(stack)) {
					if(stack.stackSize <= maxStackSize) {
						targetSlot.putStack(stack.copy());
						targetSlot.onSlotChanged();
						stack.stackSize = 0;
						didMerge = true;
						break;
					} else {
						ItemStack newStack = stack.copy();
						newStack.stackSize = maxStackSize;
						targetSlot.putStack(newStack);
						targetSlot.onSlotChanged();
						stack.stackSize -= maxStackSize;
						didMerge = true;
					}
				}

				if(toPlayerInventory) {
					--targetIndex;
				} else {
					++targetIndex;
				}
			}
		}

		return didMerge;
	}

}
