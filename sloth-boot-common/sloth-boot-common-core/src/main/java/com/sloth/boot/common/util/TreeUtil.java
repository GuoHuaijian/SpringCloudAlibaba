package com.sloth.boot.common.util;

import cn.hutool.core.collection.CollUtil;
import com.sloth.boot.common.base.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形结构工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class TreeUtil {

    private TreeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 构建树形结构。
     * <p>
     * 父节点缺失（孤儿）或自引用的节点会被提升为根节点，避免数据被静默丢弃；
     * 节点 ID 重复视为非法输入，快速失败。
     *
     * @param list   节点列表
     * @param rootId 根节点ID
     * @param <T>    节点类型
     * @return 树形结构列表
     */
    public static <T extends TreeNode> List<T> buildTree(List<T> list, Long rootId) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }

        // 转换为 Map，方便查找；重复 ID 快速失败
        Map<Long, T> nodeMap = new HashMap<>(list.size());
        for (T node : list) {
            T previous = nodeMap.put(node.getId(), node);
            if (previous != null) {
                throw new IllegalArgumentException("重复的树节点ID: " + node.getId());
            }
            // 清空历史子节点，防止重复构建时子节点不断累积
            if (node.getChildren() != null) {
                node.getChildren().clear();
            }
        }

        // 构建树形结构
        List<T> treeList = new ArrayList<>();
        for (T node : list) {
            Long parentId = node.getParentId();
            T parent = parentId == null ? null : nodeMap.get(parentId);
            if (parent == null || parent == node || parentId.equals(rootId)) {
                // 根节点 / 父节点缺失 / 自引用：提升为根，保证节点不丢失
                treeList.add(node);
            } else {
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            }
        }

        return treeList;
    }

    /**
     * 展开树形结构为扁平列表
     *
     * @param tree 树形结构
     * @param <T>  节点类型
     * @return 扁平列表
     */
    public static <T extends TreeNode> List<T> flattenTree(List<T> tree) {
        List<T> flatList = new ArrayList<>();
        if (CollUtil.isEmpty(tree)) {
            return flatList;
        }

        for (T node : tree) {
            appendNode(flatList, node);
        }

        return flatList;
    }

    @SuppressWarnings("unchecked")
    private static <T extends TreeNode> void appendNode(List<T> flatList, TreeNode node) {
        flatList.add((T) node);
        if (CollUtil.isEmpty(node.getChildren())) {
            return;
        }
        for (TreeNode child : node.getChildren()) {
            appendNode(flatList, child);
        }
    }
}
