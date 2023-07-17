package lotr.common.fellowshipQuests;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lotr.common.fellowship.LOTRFellowship;
import lotr.common.fellowship.LOTRFellowshipData;
import lotr.common.fellowship.ROMEFellowShip;
import lotr.common.fellowshipQuests.quests.FCompletedQuest;
import lotr.common.fellowshipQuests.quests.FQuestKillMobs;
import lotr.common.libs.ROMELib;
import lotr.rome.ExtendedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class QuestRegister {

    public static final QuestRegister INSTANCE = new QuestRegister();
//
    public static final List<Class> quests = new ArrayList<>();
//
//    public static Map<String, QuestReq> randomQuests = new HashMap<>();
//
    public static void registerQuests() {
//        quests.add(new FCompletedQuest("Квест завершен!", "completed", 0));
//        quests.add(new FCompletedQuest("Квеста нет", "noQuest", 0));
//        quests.add(new FQuestKillMobs("Ебнуть кротов", "killMobsTier1", 10));
//        quests.add(new FQuestKillMobs("Ебнуть кротов2", "killMobsTier2", 20));
//        quests.add(new FQuestKillMobs("Ебнуть кротов3", "killMobsTier3", 30));
//
//
//        randomQuests.put("killMobsTier1", new QuestReq(0, 0.5F));
//        randomQuests.put("killMobsTier2", new QuestReq(0, 0.5F));
//        randomQuests.put("killMobsTier3", new QuestReq(0, 0.5F));
//
        quests.add(FCompletedQuest.class);
        quests.add(FQuestKillMobs.class);
    }


    public static int secondForQuest = 100;
//
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (LOTRFellowship fs : LOTRFellowshipData.fellowshipMap.values()) {

            Instant lastQuestTime = Instant.now();
            if(!fs.getLastQuestDate().equals("")) {
                lastQuestTime = Instant.parse(fs.getLastQuestDate());
            } else {
                fs.setLastQuestDate(Instant.now().toString());

                fs.setQuest(new FQuestKillMobs(fs.getFellowshipID()), 1);
            }
            if(lastQuestTime == null) {
                lastQuestTime = Instant.now();
                fs.setLastQuestDate(Instant.now().toString());
            }
            Instant currentTime = Instant.now();

            if (Duration.between(lastQuestTime, currentTime).getSeconds() >= secondForQuest) {
                fs.setLastQuestDate(currentTime.toString());
                fs.setQuest(new FQuestKillMobs(fs.getFellowshipID()), 1);
            }
        }
    }

    private static List<QuestBase> doQuestsLogic(EntityEvent event, Entity actor) {
        if (actor instanceof EntityPlayer ) {
            EntityPlayer ply = (EntityPlayer) actor;

            if (!Objects.equals(ExtendedPlayer.get(ply).getMainFellowShip(), "")) {

                if(ROMELib.getFellowShipServer(ply) != null) {

                    return getData(ROMELib.getFellowShipServer(ply).getQuestData());
                }
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onLivingUpdateFellowshipQuest(LivingEvent.LivingUpdateEvent event) {

//        List<QuestBase> quests = doQuestsLogic(event, event.entity);
//
//        if(quests != null) {
//            for(QuestBase q : quests) {
//                q.doUpdate(event);
//                //Objects.requireNonNull(ROMELib.getFellowShipServer((EntityPlayer) event.entity)).markDirtyData();
//            }
//        }
    }

    @SubscribeEvent
    public void onDeathFellowshipQuest(LivingDeathEvent event) {

        List<QuestBase> quests = doQuestsLogic(event, event.source.getEntity());

        if(quests != null) {
            for(QuestBase q : quests) {
                q.doDeath(event);
                event.entity.attackEntityFrom(DamageSource.fall, 5F);
            }
        }

    }


    public static ArrayList<QuestBase> getData(byte[] data) {
        Kryo kryo = new Kryo();
        kryo.register(ArrayList.class);
        kryo.register(QuestBase.class);
        kryo.addDefaultSerializer(UUID.class, new Serializer<UUID>() {
            @Override
            public void write(Kryo kryo, Output output, UUID uuid) {
                output.writeLong(uuid.getMostSignificantBits());
                output.writeLong(uuid.getLeastSignificantBits());
            }

            @Override
            public UUID read(Kryo kryo, Input input, Class<? extends UUID> type) {
                return new UUID(input.readLong(), input.readLong());
            }

        });
        kryo.register(UUID.class);
        for(Class c : quests) {
            kryo.register(c);
        }
        Input input = new ByteBufferInput(data);
        ArrayList<QuestBase> DD = kryo.readObject(input, ArrayList.class);
        System.out.println(DD.get(1).getCompletedObjective());
        return DD;
    }

    public static byte[] serializeData(ArrayList<QuestBase> list) {
        Kryo kryo = new Kryo();
        kryo.register(ArrayList.class);
        kryo.register(QuestBase.class);
        kryo.addDefaultSerializer(UUID.class, new Serializer<UUID>() {
            @Override
            public void write(Kryo kryo, Output output, UUID uuid) {
                output.writeLong(uuid.getMostSignificantBits());
                output.writeLong(uuid.getLeastSignificantBits());
            }

            @Override
            public UUID read(Kryo kryo, Input input, Class<? extends UUID> type) {
                return new UUID(input.readLong(), input.readLong());
            }

        });
        kryo.register(UUID.class);
        for(Class c : quests) {
            kryo.register(c);
        }
        Output output = new ByteBufferOutput(4096, -1);
        kryo.writeObject(output, list);
        return output.toBytes();
    }
}
