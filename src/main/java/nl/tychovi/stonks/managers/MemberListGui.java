package nl.tychovi.stonks.managers;

import com.j256.ormlite.stmt.QueryBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import nl.tychovi.stonks.Database.Company;
import nl.tychovi.stonks.Database.Member;
import nl.tychovi.stonks.gui.CompanyInfoGui;
import nl.tychovi.stonks.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MemberListGui implements InventoryProvider {

    public static DatabaseManager databaseManager;
    public static InventoryManager inventoryManager;

    private Company company;

    public MemberListGui(Company company) {
        this.company = company;
    }

    public static SmartInventory getInventory(Company company) {
        return SmartInventory.builder()
                .id("memberList")
                .provider(new MemberListGui(company))
                .manager(inventoryManager)
                .size(5, 9)
                .title(company.getName() + " Members")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));
        contents.fillRow(4, ClickableItem.empty(Util.item(Material.BLACK_STAINED_GLASS_PANE, " ")));

        contents.set(0, 4, ClickableItem.empty(Util.item(Material.getMaterial(company.getLogoMaterial()), company.getName())));

        Pagination pagination = contents.pagination();

        List<Member> list = null;
        try {
            QueryBuilder<Member, UUID> queryBuilder = databaseManager.getMemberDao().queryBuilder();
            queryBuilder.where().eq("company_id", company.getId()).and().eq("acceptedInvite", true);
            list = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ClickableItem[] items = new ClickableItem[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Member member = list.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            ClickableItem item = ClickableItem.empty(Util.playerHead(offlinePlayer.getName(), offlinePlayer, "Role: " + member.getRole().toString()));
            items[i] = item;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));


        contents.set(4, 3, ClickableItem.of(Util.item(Material.ARROW, "Previous page"),
                e -> getInventory(company).open(player, pagination.previous().getPage())));
        contents.set(4, 5, ClickableItem.of(Util.item(Material.ARROW, "Next page"),
                e -> getInventory(company).open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
