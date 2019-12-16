package nvcs.model;

import java.util.Collections;
import java.util.List;

/**
 * A class represents imported project.
 */
public class Project {

    protected final ProjectNode root;
    protected final String projectDir;

    public Project(ProjectNode root, String projectDir) {
        this.root = root;
        this.projectDir = projectDir;
    }

    public ProjectNode getRoot() {
        return root;
    }

    public String getProjectDir() {
        return projectDir;
    }

    /**
     * A class represents single project node: file or directory.
     */
    public static class ProjectNode {

        protected final String name;
        protected final boolean directory;

        protected final ProjectNode parent;
        protected List<ProjectNode> children;

        public ProjectNode(String name, boolean directory, ProjectNode parent) {
            this.name = name;
            this.directory = directory;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public boolean isDirectory() {
            return directory;
        }

        public List<ProjectNode> getChildren() {
            if (!isDirectory()) {
                throw new IllegalStateException("File cannot have children");
            }
            return Collections.unmodifiableList(children);
        }

        public void setChildren(List<ProjectNode> children) {
            if (!isDirectory()) {
                throw new IllegalStateException("Cannot set children for the 'File' node");
            }
            if (this.children != null) {
                throw new IllegalStateException("Cannot set children when already initialized");
            }
            this.children = children;
        }
    }
}
