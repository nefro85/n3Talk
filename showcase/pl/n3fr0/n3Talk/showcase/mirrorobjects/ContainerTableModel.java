package pl.n3fr0.n3Talk.showcase.mirrorobjects;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import pl.n3fr0.n3talk.mirror.MirrorContainer;
import pl.n3fr0.n3talk.mirror.MirrorContainerListenerAdapter;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;

public class ContainerTableModel extends AbstractTableModel {

    public ContainerTableModel(MirrorContainer container) {
        this.container = container;
        this.container.setListener(new MirrorContainerListenerAdapter() {

            @Override
            public void onChange() {
                init();
            }
        });
        init();
    }

    private void init() {
        rects.clear();
        for (MirrorBase b : container.getObjects()) {
            rects.add((Rectangle) b);
        }
        fireTableDataChanged();
    }

    public int getColumnCount() {
        return columnsNames.length;
    }

    public int getRowCount() {
        return rects.size();
    }

    public Rectangle getRectangle(int index) {
        return rects.get(index);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        final Rectangle r = rects.get(rowIndex);
        Object o = null;

        switch (columnIndex) {
            case 0:
                o = r.getObjectUID();
                break;
            case 1:
                o = r.getSizeX();
                break;
            case 2:
                o = r.getSizeY();
                break;
        }

        return o;
    }

    @Override
    public String getColumnName(int column) {
        return columnsNames[column];
    }
    final private List<Rectangle> rects = new ArrayList<Rectangle>();
    private MirrorContainer container;
    final private String[] columnsNames = new String[]{
        "UID", "sizeX", "sizeY"
    };
}
