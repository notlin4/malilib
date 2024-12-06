package fi.dy.masa.malilib.util.position;

import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public enum Direction
{
    DOWN (0, 1, -1, Axis.Y, AxisDirection.NEGATIVE, "down", net.minecraft.util.math.Direction.DOWN),
    UP   (1, 0, -1, Axis.Y, AxisDirection.POSITIVE, "up", net.minecraft.util.math.Direction.UP),
    NORTH(2, 3, 2, Axis.Z, AxisDirection.NEGATIVE, "north", net.minecraft.util.math.Direction.NORTH),
    SOUTH(3, 2, 0, Axis.Z, AxisDirection.POSITIVE, "south", net.minecraft.util.math.Direction.SOUTH),
    WEST (4, 5, 1, Axis.X, AxisDirection.NEGATIVE, "west", net.minecraft.util.math.Direction.WEST),
    EAST (5, 4, 3, Axis.X, AxisDirection.POSITIVE, "east", net.minecraft.util.math.Direction.EAST);

    public static final Direction[] ALL_DIRECTIONS = new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    public static final Direction[] HORIZONTALS_BY_INDEX = new Direction[] { Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST };
    public static final Direction[] VERTICAL_DIRECTIONS = new Direction[] { Direction.DOWN, Direction.UP };

    private final int index;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final int oppositeId;
    private final int horizontalIndex;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final net.minecraft.util.math.Direction vanillaDirection;
    private final String name;
    private final String translationKey;

    Direction(int index, int oppositeId, int horizontalIndex, Axis axis, AxisDirection axisDirection, String name, net.minecraft.util.math.Direction vanillaDirection)
    {
        this.index = index;
        this.offsetX = axis == Axis.X ? axisDirection.getOffset() : 0;
        this.offsetY = axis == Axis.Y ? axisDirection.getOffset() : 0;
        this.offsetZ = axis == Axis.Z ? axisDirection.getOffset() : 0;
        this.oppositeId = oppositeId;
        this.horizontalIndex = horizontalIndex;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.name = name;
        this.translationKey = MaLiLibReference.MOD_ID + ".label.direction." + name;
        this.vanillaDirection = vanillaDirection;
    }

    public int getIndex()
    {
        return this.index;
    }

    public Axis getAxis()
    {
        return this.axis;
    }

    public AxisDirection getAxisDirection()
    {
        return this.axisDirection;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    public int getXOffset()
    {
        return this.offsetX;
    }

    public int getYOffset()
    {
        return this.offsetY;
    }

    public int getZOffset()
    {
        return this.offsetZ;
    }

    public Direction getOpposite()
    {
        return ALL_DIRECTIONS[this.oppositeId];
    }

    public net.minecraft.util.math.Direction getVanillaDirection()
    {
        return this.vanillaDirection;
    }

    /**
     * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public Direction rotateY()
    {
        return switch (this)
        {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            default -> this;
        };

    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public Direction rotateYCCW()
    {
        return switch (this)
        {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
            default -> this;
        };

    }

    public Direction rotateAround(Axis axis)
    {
        return switch (axis)
        {
            case X ->
            {
                if (this != WEST && this != EAST)
                {
                    yield this.rotateX();
                }
                yield this;
            }
            case Y ->
            {
                if (this != UP && this != DOWN)
                {
                    yield this.rotateY();
                }
                yield this;
            }
            case Z ->
            {
                if (this != NORTH && this != SOUTH)
                {
                    yield this.rotateZ();
                }
                yield this;
            }
        };

    }

    /**
     * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    public Direction rotateX()
    {
        return switch (this)
        {
            case NORTH -> DOWN;
            case DOWN -> SOUTH;
            case SOUTH -> UP;
            case UP -> NORTH;
            default -> this;
        };

    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    public Direction rotateZ()
    {
        return switch (this)
        {
            case EAST -> DOWN;
            case DOWN -> WEST;
            case WEST -> UP;
            case UP -> EAST;
            default -> this;
        };

    }

    public Direction cycle(boolean reverse)
    {
        return reverse ? this.cycleBackward() : this.cycleForward();
    }

    public Direction cycleForward()
    {
        int index = this.index;
        index = index >= 5 ? 0 : index + 1;
        return ALL_DIRECTIONS[index];
    }

    public Direction cycleBackward()
    {
        int index = this.index;
        index = index == 0 ? 5 : index - 1;
        return ALL_DIRECTIONS[index];
    }

    public static Direction byIndex(int index)
    {
        return ALL_DIRECTIONS[index % 6];
    }

    public static Direction byHorizontalIndex(int horizontalIndexIn)
    {
        return HORIZONTALS_BY_INDEX[horizontalIndexIn & 3];
    }

    public static Direction of(net.minecraft.util.math.Direction facing)
    {
        return byIndex(facing.getId());
    }

    /**
     * "Get the Direction corresponding to the given angle in degrees (0-360).
     * Out of bounds values are wrapped around.
     * An angle of 0 is SOUTH, an angle of 90 would be WEST."
     */
    public static Direction fromAngle(double angle)
    {
        return byHorizontalIndex(MathUtils.floor(angle / 90.0 + 0.5) & 3);
    }

    /**
     * Gets the angle in degrees corresponding to this Direction.
     */
    public float getHorizontalAngle()
    {
        return (float)((this.horizontalIndex & 3) * 90);
    }

    public enum Axis
    {
        X("x", false),
        Y("y", true),
        Z("z", false);

        public static final Axis[] ALL_AXES = new Axis[] { X, Y, Z };

        private final String name;
        private final boolean isVertical;

        Axis(String name, boolean isVertical)
        {
            this.name = name;
            this.isVertical = isVertical;
        }

        public String getName()
        {
            return this.name;
        }

        public boolean isHorizontal()
        {
            return !this.isVertical;
        }

        public boolean isVertical()
        {
            return this.isVertical;
        }

        public static Axis byName(String name)
        {
            return switch (name)
            {
                case "x" -> X;
                case "y" -> Y;
                case "z" -> Z;
                default -> X;
            };

        }
    }

    public enum AxisDirection
    {
        NEGATIVE(-1),
        POSITIVE(1);

        private final int offset;

        AxisDirection(int offset)
        {
            this.offset = offset;
        }

        public int getOffset()
        {
            return this.offset;
        }
    }
}
