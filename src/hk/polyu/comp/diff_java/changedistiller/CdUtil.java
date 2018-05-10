package hk.polyu.comp.diff_java.changedistiller;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import com.sun.java.accessibility.util.AccessibilityListenerList;
import hk.polyu.comp.diff_java.SimpleLogger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liushanchen on 16/8/10.
 */
public class CdUtil {
    public static int ALL = 1;
    public static int INSERT = 1;
    public static int UPDATE = 1;
    public static int DELETE = 1;

    public static StructureEntityVersion isDesiredChange(File left, File right) {
        if (left != null && right != null) {
            Set<StructureEntityVersion> root = new HashSet<>();
            Set<StructureEntityVersion> methods = new HashSet<>();
            int countDesired = 0, countI = 0, countU = 0, countD = 0;
            List<SourceCodeChange> changes = getChanges(left, right);
            if (changes != null && changes.size() > 0) {
                for (SourceCodeChange change : changes) {
                    StructureEntityVersion rootEntity = change.getRootEntity();
                    EntityType rootType = rootEntity.getType();
                    if (rootType.isClass() || rootType.isField()) return null;
                    if (rootType.isMethod()) methods.add(rootEntity);
                    else root.add(rootEntity);
//                    if (change.getChangedEntity().getType().equals(JavaEntityType.JAVADOC)) continue;
                    SimpleLogger.info(change.toString());

                    switch (change.getChangeType()) {
                        case DOC_DELETE:
                        case DOC_INSERT:
                        case DOC_UPDATE:
                        case COMMENT_MOVE:
                        case COMMENT_DELETE:
                        case COMMENT_INSERT:
                        case COMMENT_UPDATE:
                        case ALTERNATIVE_PART_DELETE:
                        case ALTERNATIVE_PART_INSERT:
                            continue;
                        case STATEMENT_DELETE: {
                            countD++;
                            countDesired++;
                            break;
                        }
                        case STATEMENT_INSERT: {
                            countI++;
                            countDesired++;
                            break;
                        }
                        case STATEMENT_ORDERING_CHANGE: {
                            countU++;
                            countDesired++;
                            break;
                        }
                        case STATEMENT_PARENT_CHANGE: {
                            countU++;
                            countDesired++;
                            break;
                        }
                        case STATEMENT_UPDATE: {
                            countDesired++;
                            countU++;
                            break;
                        }
                        case CONDITION_EXPRESSION_CHANGE: {
                            countDesired++;
                            countU++;
                            break;
                        }
                        default:
                            SimpleLogger.info("getChangeType: " + change.getChangeType().toString());
                            return null;
                    }
                }
                if (methods.size() == 1 && countDesired > 0 && countDesired <= ALL &&
                        (countI <= INSERT && countU <= UPDATE && countD <= DELETE))
                    for (StructureEntityVersion r : methods) {
                        if (r.getType().isMethod())
                            return r;
                    }
            }
            SimpleLogger.info("countDesired: " + countDesired);
        } else {
            SimpleLogger.info("left == null || right == null: ");
        }
        return null;
    }


    private static List<SourceCodeChange> getChanges(File left, File right) {
        FileDistiller distiller = ChangeDistiller.createFileDistiller(ChangeDistiller.Language.JAVA);
        try {
            distiller.extractClassifiedSourceCodeChanges(left, right);
        } catch (Exception e) {
            /* An exception most likely indicates a bug in ChangeDistiller.*/
            System.err.println("Warning: error while change distilling. " + e.getMessage());
        }
        return distiller.getSourceCodeChanges();
    }


}
