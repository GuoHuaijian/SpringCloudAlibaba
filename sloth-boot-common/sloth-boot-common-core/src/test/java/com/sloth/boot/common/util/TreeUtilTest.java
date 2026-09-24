package com.sloth.boot.common.util;

import com.sloth.boot.common.base.TreeNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 树形结构工具类测试。
 */
@DisplayName("TreeUtil 测试")
class TreeUtilTest {

    private TreeNode node(Long id, Long parentId) {
        TreeNode treeNode = new TreeNode();
        treeNode.setId(id);
        treeNode.setParentId(parentId);
        return treeNode;
    }

    @Test
    @DisplayName("构建树形结构：根节点与子节点正确挂载")
    void buildTreeBasic() {
        List<TreeNode> nodes = List.of(node(1L, 0L), node(2L, 1L), node(3L, 1L));

        List<TreeNode> tree = TreeUtil.buildTree(nodes, 0L);

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getId()).isEqualTo(1L);
        assertThat(tree.get(0).getChildren()).hasSize(2);
    }

    @Test
    @DisplayName("父节点缺失的孤儿节点提升为根，不被静默丢弃")
    void buildTreePromotesOrphan() {
        List<TreeNode> nodes = List.of(node(1L, 0L), node(2L, 999L));

        List<TreeNode> tree = TreeUtil.buildTree(nodes, 0L);

        assertThat(tree).extracting(TreeNode::getId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("自引用节点提升为根，不形成自环")
    void buildTreePromotesSelfReference() {
        List<TreeNode> nodes = List.of(node(1L, 1L));

        List<TreeNode> tree = TreeUtil.buildTree(nodes, 0L);

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getChildren()).isNull();
    }

    @Test
    @DisplayName("重复节点 ID 快速失败")
    void buildTreeRejectsDuplicateId() {
        List<TreeNode> nodes = List.of(node(1L, 0L), node(1L, 0L));

        assertThatThrownBy(() -> TreeUtil.buildTree(nodes, 0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("1");
    }

    @Test
    @DisplayName("重复构建不导致子节点累积")
    void buildTreeIsIdempotent() {
        List<TreeNode> nodes = List.of(node(1L, 0L), node(2L, 1L));

        TreeUtil.buildTree(nodes, 0L);
        List<TreeNode> tree = TreeUtil.buildTree(nodes, 0L);

        assertThat(tree.get(0).getChildren()).hasSize(1);
    }

    @Test
    @DisplayName("展开树形结构为扁平列表")
    void flattenTree() {
        List<TreeNode> nodes = List.of(node(1L, 0L), node(2L, 1L), node(3L, 2L));
        List<TreeNode> tree = TreeUtil.buildTree(nodes, 0L);

        List<TreeNode> flat = TreeUtil.flattenTree(tree);

        assertThat(flat).extracting(TreeNode::getId).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("空列表返回空树")
    void buildTreeEmptyInput() {
        assertThat(TreeUtil.buildTree(List.of(), 0L)).isEmpty();
        assertThat(TreeUtil.buildTree(null, 0L)).isEmpty();
    }
}
