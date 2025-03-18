package me.howandev.nexus.integration.vault.provider;

import me.howandev.nexus.NexusPlugin;
import me.howandev.nexus.configuration.impl.file.FileConfiguration;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.List;

public class NexusEconomy implements Economy {
    public static DecimalFormat FORMAT = new DecimalFormat("##,####0");
    private FileConfiguration economy;
    public NexusEconomy() {
        this.economy = NexusPlugin.getInstance().getEconomyStorage();
    }


    @Override
    public boolean isEnabled() {
        return NexusPlugin.getInstance() != null && NexusPlugin.getInstance().isEnabled();
    }

    @Override
    public String getName() {
        return "Nexus Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return FORMAT.format(v);
    }

    @Override
    public String currencyNamePlural() {
        return "$";
    }

    @Override
    public String currencyNameSingular() {
        return "$";
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public boolean hasAccount(String s) {
        return economy.isSet(s + ".balance");
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return economy.isSet(offlinePlayer.getName() + ". balance");
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public boolean hasAccount(String s, String s1) {
        return economy.isSet(s + ". balance");
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return economy.isSet(offlinePlayer.getName() + ". balance");
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public double getBalance(String s) {
        return economy.getDouble(s + ".balance", 0d);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return economy.getDouble(offlinePlayer.getName() + ".balance", 0d);
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public double getBalance(String s, String s1) {
        return economy.getDouble(s + ".balance", 0d);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return economy.getDouble(offlinePlayer.getName() + ".balance", 0d);
    }

    /**
     * @param s
     * @param v
     * @deprecated
     */
    @Override
    public boolean has(String s, double v) {
        return getBalance(s) >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return getBalance(offlinePlayer) >= v;
    }

    /**
     * @param s
     * @param s1
     * @param v
     * @deprecated
     */
    @Override
    public boolean has(String s, String s1, double v) {
        return getBalance(s) >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return getBalance(offlinePlayer) >= v;
    }

    private boolean setBalance(String s, double v) {
        economy.set(s + ".balance", v);
        return true;
    }

    private boolean setBalance(OfflinePlayer offlinePlayer, double v) {
        return setBalance(offlinePlayer.getName(), v);
    }

    /**
     * @param s
     * @param v
     * @deprecated
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        if (s == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "eval s == null");

        if (v < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "eval v < 0");

        var balance = getBalance(s);
        if (balance - v < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "eval balance - v < 0");

        setBalance(s, balance - v);
        return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    /**
     * @param s
     * @param s1
     * @param v
     * @deprecated
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    /**
     * @param s
     * @param v
     * @deprecated
     */
    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        if (s == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "eval s == null");

        if (v < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "eval v < 0");

        var balance = getBalance(s);
        if (balance + v > Double.MAX_VALUE) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "eval balance + v < 0");

        setBalance(s, balance + v);

        return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    /**
     * @param s
     * @param s1
     * @param v
     * @deprecated
     */
    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "UnsupportedOperationException");
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public boolean createPlayerAccount(String s) {
        economy.set(s + ".balance", 0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        economy.set(offlinePlayer.getName() + ".balance", 0);
        return true;
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        economy.set(s + ".balance", 0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        economy.set(offlinePlayer.getName() + ".balance", 0);
        return true;
    }
}
