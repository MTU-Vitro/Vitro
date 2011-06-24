package vitro;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class DataView {
	private final StructWrapper root;

	public DataView(Object o, ColorScheme colorScheme) {
		root = StructWrapper.build(o, colorScheme);
	}

	public void draw(Graphics g) { root.draw(g, 0, 0); }
	public int width()           { return root.width(); }
	public int height()          { return root.height(); }

	private static abstract class StructWrapper {
		public final List<StructWrapper> children = new ArrayList<StructWrapper>();
		private Integer fixedWidth  = null;
		private Integer fixedHeight = null;
		protected final ColorScheme colorScheme;

		protected StructWrapper(ColorScheme colorScheme) {
			this.colorScheme = colorScheme;
		}

		public static StructWrapper build(Object value, ColorScheme colorScheme) {
			return build(value, 0, colorScheme);
		}

		public static StructWrapper build(Object value, int depth, ColorScheme colorScheme) {
			if (value instanceof Map)        { return new MapWrapper((Map)value, depth, colorScheme); }
			if (value instanceof Map.Entry)  { return new EntryWrapper((Map.Entry)value, depth, colorScheme); }
			if (value instanceof Collection) {
				Collection collection = (Collection)value;
				if (collection.size() < 1) { return new AtomWrapper("{}", depth, colorScheme); }
				return new ListWrapper(collection, depth, colorScheme);
			}
			if (value instanceof Object[][]) {
				Object[][] grid = (Object[][])value;
				if (grid.length == 0 || grid[0].length == 0) { return buildArray(grid, depth, colorScheme); }
				if (grid[0] instanceof Object[][])           { return buildArray(grid, depth, colorScheme); }
				for(Object[] row : grid) {
					if (row.length != grid[0].length) { return buildArray(grid, depth, colorScheme); }
				}
				return new GridWrapper(grid, depth, colorScheme);
			}
			if (value instanceof Object[])   { return buildArray((Object[])value, depth, colorScheme); }
			if (value instanceof int[]) {
				// There really ought to be a better way to do this.
				// The current approach will need 6 more overloads.
				// (boolean[], char[], short[], long[], float[], double[])
				int[] array = (int[])value;
				Integer[] i = new Integer[array.length];
				for(int x = 0; x < array.length; x++) {
					i[x] = array[x];
				}
				return build(i, depth, colorScheme);
			}
			if (value instanceof Iterable) {
				List<Object> collection = new ArrayList<Object>();
				for(Object o : (Iterable)value) {
					collection.add(o);
				}
				return build(collection, depth, colorScheme);
			}
			return new AtomWrapper(value, depth, colorScheme);
		}
		private static StructWrapper buildArray(Object[] value, int depth, ColorScheme colorScheme) {
			if (value.length == 0) { return new AtomWrapper("[]", depth, colorScheme); }
			return new ArrayWrapper(value, depth, colorScheme);
		}

		public void setWidth(int width) {
			if (width < actualWidth()) { return; }
			fixedWidth = width;
		}
		public void setHeight(int height) {
			if (height < actualHeight()) { return; }
			fixedHeight = height;
		}
		protected int totalWidth() {
			int ret = 0;
			for(StructWrapper child : children) { ret += child.width(); }
			return ret;
		}
		protected int totalHeight() {
			int ret = 0;
			for(StructWrapper child : children) { ret += child.height(); }
			return ret;
		}
		protected int maxWidth() {
			int ret = 0;
			for(StructWrapper child : children) { ret = Math.max(ret, child.width()); }
			return ret;
		}
		protected int maxHeight() {
			int ret = 0;
			for(StructWrapper child : children) { ret = Math.max(ret, child.height()); }
			return ret;
		}
		public final int width() {
			if (fixedWidth != null) { return fixedWidth; }
			return actualWidth();
		}
		public final int height() {
			if (fixedHeight != null) { return fixedHeight; }
			return actualHeight();
		}

		public abstract int actualWidth();
		public abstract int actualHeight();
		public abstract void draw(Graphics g, int x, int y);
	}

	private static class ListWrapper extends StructWrapper {
		private static final int margin = 5;

		public ListWrapper(Collection list, int depth, ColorScheme colorScheme) {
			super(colorScheme);
			for(Object o : list) { children.add(build(o, depth+1, colorScheme)); }
			int w = maxWidth();
			for(StructWrapper child : children) {
				child.setWidth(w);
			}
		}
		public void setWidth(int width) {
			for(StructWrapper child : children) {
				child.setWidth(width - (2 * margin));
			}
		}
		public int actualWidth() {
			return maxWidth() + (2 * margin);
		}
		public int actualHeight() {
			return totalHeight() + (margin * (children.size()+1));
		}
		public void draw(Graphics g, int x, int y) {
			g.setColor(colorScheme.inactive);
			g.fillRect(x+1, y+1, width()-1, height()-1);
			for(StructWrapper child : children) {
				child.draw(g, x + margin, y + margin);
				y += child.height() + margin;
			}
		}
	}

	private static class ArrayWrapper extends StructWrapper {
		public ArrayWrapper(Object[] array, int depth, ColorScheme colorScheme) {
			super(colorScheme);
			for(Object o : array) { children.add(build(o, depth+1, colorScheme)); }
		}
		public void setWidth(int width) {
			for(StructWrapper child : children) {
				if (child instanceof AtomWrapper) { continue; }
				child.setWidth(width / children.size());
			}
			super.setWidth(width);
		}
		public void setHeight(int height) {
			for(StructWrapper child : children) {
				if (child instanceof AtomWrapper) { continue; }
				child.setHeight(height);
			}
			super.setHeight(height);
		}
		public int actualWidth() {
			return maxWidth() * children.size();
		}
		public int actualHeight() {
			return maxHeight();
		}
		public void draw(Graphics g, int x, int y) {
			int width = width();
			int w = width / children.size();
			int h = height();
			int tw = 0;
			for(int index = 0; index < children.size(); index++) {
				// The last cell must be treated as a special case,
				// since the desired width may not be evenly divisible
				// by the cell width.
				int cw = (index == children.size()-1) ? width-tw : w;
				
				g.setColor(colorScheme.background);       
				g.fillRect(x, y, cw, h);
				g.setColor(colorScheme.outline);
				g.drawRect(x, y, cw, h);
				StructWrapper child = children.get(index);
				child.draw(
					g,
					x + (w - child.width())/2,
					y + (h - child.height())/2
				);
				x  += w;
				tw += w;
			}
		}
	}

	private static class GridWrapper extends StructWrapper {
		private final StructWrapper[][] grid;
		public GridWrapper(Object[][] grid, int depth, ColorScheme colorScheme) {
			super(colorScheme);
			this.grid = new StructWrapper[grid.length][grid[0].length];
			for(int a = 0; a < grid.length; a++) {
				for(int b = 0; b < grid[0].length; b++) {
					StructWrapper child = build(grid[a][b], depth+1, colorScheme);
					this.grid[a][b] = child;
					children.add(child);
				}
			}
		}
		public void setWidth(int width) {
			for(StructWrapper child : children) {
				if (child instanceof AtomWrapper) { continue; }
				child.setWidth(width / grid[0].length);
			}
			super.setWidth(width);
		}
		public void setHeight(int height) {
			for(StructWrapper child : children) {
				if (child instanceof AtomWrapper) { continue; }
				child.setHeight(height / grid.length);
			}
			super.setHeight(height);
		}
		public int actualWidth() {
			return maxWidth() * grid[0].length;
		}
		public int actualHeight() {
			return maxHeight() * grid.length;
		}
		public void draw(Graphics g, int x, int y) {
			int w = width();
			int h = height();
			int heightLeft = h;
			int cw = w / grid[0].length;
			int ch = h / grid.length;

			g.setColor(colorScheme.background);
			g.fillRect(x, y, w, h);
			for(int a = 0; a < grid.length; a++) {
				int widthLeft = w;
				for(int b = 0; b < grid[0].length; b++) {
					g.setColor(colorScheme.secondary);
					int ow = (b == grid[0].length - 1) ? widthLeft  : cw;
					int oh = (a == grid.length    - 1) ? heightLeft : ch;
					g.drawRect(x + (b*cw), y + (a*ch), ow, oh);
					StructWrapper child = grid[a][b];
					child.draw(
						g,
						x + (b*cw) + (cw - child.width())/2,
						y + (a*ch) + (ch - child.height())/2
					);
					widthLeft -= cw;
				}
				heightLeft -= ch;
			}
			g.setColor(colorScheme.outline);
			g.drawRect(x, y, w, h);
		}
	}

	private static class MapWrapper extends StructWrapper {
		public MapWrapper(Map map, int depth, ColorScheme colorScheme) {
			super(colorScheme);
			for(Object entry : map.entrySet()) { children.add(build(entry, depth+1, colorScheme)); }
			int w1 = 0;
			int w2 = 0;
			for(StructWrapper entry : children) {
				w1 = Math.max(w1, entry.children.get(0).width());
				w2 = Math.max(w2, entry.children.get(1).width());
			}
			for(StructWrapper entry : children) {
				StructWrapper a = entry.children.get(0);
				StructWrapper b = entry.children.get(1);
				a.setWidth(w1);
				b.setWidth(w2);
				int h = Math.max(a.height(), b.height());
				a.setHeight(h);
				b.setHeight(h);
			}
		}
		public void setWidth(int width) {
			for(StructWrapper entry : children) {
				entry.setWidth(width);
			}
		}
		public void setHeight(int height) {
			height -= totalHeight();
			int rowHeight = height / children.size();
			for(int x = 0; x < children.size() - 1; x++) {
				StructWrapper child = children.get(x);
				int paddedHeight = rowHeight + child.height();
				child.setHeight(paddedHeight);
			}
			StructWrapper last = children.get(children.size()-1);
			last.setHeight((height - (rowHeight * (children.size()-1))) + last.height());
		}
		public int actualWidth() {
			return maxWidth();
		}
		public int actualHeight() {
			return totalHeight();
		}
		public void draw(Graphics g, int x, int y) {
			for(StructWrapper child : children) {
				child.draw(g, x, y);
				y += child.height();
			}
		}
	}

	private static class EntryWrapper extends StructWrapper {
		private static final int keyMargin = 5;

		public EntryWrapper(Map.Entry entry, int depth, ColorScheme colorScheme) {
			super(colorScheme);
			children.add(build(entry.getKey(),   depth+0, colorScheme));
			children.add(build(entry.getValue(), depth+1, colorScheme));
		}
		public void setWidth(int width) {
			children.get(1).setWidth(width - children.get(0).width() - (2 * keyMargin));
		}
		public int actualWidth() {
			return totalWidth() + (2 * keyMargin);
		}
		public int actualHeight() {
			return maxHeight();
		}
		public void draw(Graphics g, int x, int y) {
			int h = height();
			int w1 = children.get(0).width() + (2 * keyMargin);
			int w2 = children.get(1).width();
			g.setColor(colorScheme.secondary);
			g.fillRect(x, y, w1, h);
			g.setColor(colorScheme.outline);
			g.drawRect(x, y, w1, h);
			g.setColor(colorScheme.background);
			g.fillRect(x + w1, y, w2, h);
			g.setColor(colorScheme.outline);
			g.drawRect(x + w1, y, w2, h);
			children.get(0).draw(g, x + keyMargin, y);
			children.get(1).draw(g, x + w1, y);
		}
	}

	private static class AtomWrapper extends StructWrapper {
		private static final FontRenderContext context = new FontRenderContext(
			new AffineTransform(),
			false,
			false
		);
		private static final int margin = 2;

		private final Font font;
		private final int width;
		private final int height;
		private final int base;
		private final String value;
		
		public AtomWrapper(Object atom, int depth, ColorScheme colorScheme) {
			super(colorScheme);
			this.value = (atom == null) ? "null" : atom.toString();
			this.font = new Font("Monospaced", Font.PLAIN, 20 - (depth));
			TextLayout layout = new TextLayout(value, font, context);
			Rectangle2D bounds = layout.getBounds();
			this.base = Math.round(layout.getDescent()) + 1;
			this.width  = (int)(bounds.getWidth() + (2 * margin)) + 1;
			this.height = (int)(bounds.getHeight() + base + (2 * margin));
		}
		public int actualWidth()  {
			return width;
		}
		public int actualHeight() {
			return height;
		}
		public void draw(Graphics g, int x, int y) {
			//g.setColor(new Color(255, 0, 0, 128));
			//g.fillRect(x, y, width(), height());
			g.setColor(colorScheme.outline);
			g.setFont(font);
			g.drawString(value, x + margin, y + height - base);
		}
	}
}