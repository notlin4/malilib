package fi.dy.masa.malilib.util.game.wrap;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class ItemWrap
{
    @Nullable
    public static NbtCompound getTag(ItemStack stack, @Nonnull DynamicRegistryManager registry)
    {
        return (NbtCompound) stack.toNbt(registry);
    }

    public static void setTag(ItemStack stack, @Nullable NbtCompound tag, @Nonnull DynamicRegistryManager registry)
    {
        if (!stack.isEmpty())
        {
            RegistryEntry<Item> item = stack.getRegistryEntry();
            ComponentMap map = stack.getComponents();
            int count = stack.getCount();

            if (tag != null && !tag.isEmpty())
            {
                AtomicReference<ItemStack> atomicReference = new AtomicReference<>();
                ItemStack.fromNbt(registry, tag).ifPresentOrElse((i) ->
                          atomicReference.set(i.copyAndEmpty()), () ->
                          atomicReference.set(ItemStack.EMPTY));

                ItemStack newStack = atomicReference.get();

                ComponentChanges.Builder newChanges = ComponentChanges.builder();
                Iterator<Component<?>> iter = map.stream().iterator();

                while (iter.hasNext())
                {
                    newChanges.add(iter.next());
                }

                if (newStack.isEmpty())
                {
                    newStack = new ItemStack(item, count, newChanges.build());
                    stack = newStack.copyAndEmpty();
                }
                if (!newStack.getRegistryEntry().equals(item))
                {
                    newStack = new ItemStack(item, count, newChanges.build());
                    stack = newStack.copyAndEmpty();
                }
                if (newStack.getCount() != count)
                {
                    newStack.setCount(count);
                    newStack.applyChanges(newChanges.build());
                    stack = newStack.copyAndEmpty();
                }
            }
        }
    }

    public static ItemStack fromTag(NbtCompound tag, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<ItemStack> atomicReference = new AtomicReference<>();
        ItemStack.fromNbt(registry, tag).ifPresentOrElse((i) ->
                         atomicReference.set(i.copyAndEmpty()), () ->
                         atomicReference.set(ItemStack.EMPTY));

        return atomicReference.get();
    }

    public static boolean isEmpty(ItemStack stack)
    {
        return stack.isEmpty();
    }

    public static boolean notEmpty(ItemStack stack)
    {
        return !stack.isEmpty();
    }

    public static String getStackString(ItemStack stack, @Nonnull DynamicRegistryManager registry)
    {
        if (ItemWrap.notEmpty(stack))
        {
            String id = RegistryUtils.getItemIdStr(stack.getItem());
            NbtCompound tag = ItemWrap.getTag(stack, registry);

            return String.format("[%s @ %d - display: %s - NBT: %s] (%s)",
                                 id,
                                 stack.getDamage(),
                                 stack.getName().getString(),
                                 tag != null ? tag.toString() : "<no NBT>", stack);
        }

        return "<empty>";
    }
}
