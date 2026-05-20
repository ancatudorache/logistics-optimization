//import si config
const express= require('express');
require('dotenv').config();
const db=require('./db');
require('dotenv').config();


const app=express();
app.use(express.json());


//endpoints

//login
app.post('/api/login',async(req,res)=>{
    const{username, password}=req.body;


try {
    const [rows] = await db.execute(
        'SELECT id, name, username, role FROM users WHERE username = ? AND password = ?',
        [username, password]
    );

    if (rows.length === 0) {
    return res.status(401).json({ error: 'Username sau parola incorecte' });
}

    res.json({ message: 'Login reusit', user: rows[0] });
} catch (err) {
    res.status(500).json({ error: 'Eroare server: ' + err.message });
}

if (!username || !password) {
    return res.status(400).json({ error: 'Username și parola sunt obligatorii' });
}


});

// add delivery
app.post('/api/deliveries', async (req, res) => {
        console.log('Received delivery:', req.body);
    const { pickup_address, delivery_address, driver_id, vehicle_id, estimated_time, estimated_cost } = req.body;

    if (!delivery_address || !driver_id || !vehicle_id) {
        return res.status(400).json({ error: 'Câmpuri obligatorii lipsesc' });
    }

    try {
        const [result] = await db.execute(
            'INSERT INTO deliveries (pickup_address, delivery_address, driver_id, vehicle_id, estimated_time, estimated_cost) VALUES (?, ?, ?, ?, ?, ?)',
            [pickup_address, delivery_address, driver_id, vehicle_id, estimated_time, estimated_cost]
        );
        res.status(201).json({ message: 'Livrare adaugata cu succes', deliveryId: result.insertId });
    } catch (err) {
        console.error('Database error:', err);
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});
// get drivers
app.get('/api/drivers', async (req, res) => {
    try {
        const [rows] = await db.execute(`
            SELECT d.id, u.name, u.username
            FROM drivers d
            JOIN users u ON d.user_id = u.id
        `);
        res.json(rows);
    } catch (err) {
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});
// get vehicles
app.get('/api/vehicles', async (req, res) => {
    try {
        const [rows] = await db.execute(`SELECT id, model, plate_number, fuel_consumption, fuel_type FROM vehicles`);

        res.json(rows);
    } catch (err) {
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});
//get deliveries
app.get('/api/deliveries/driver/:id', async (req, res) => {
    const userId = req.params.id;

    try {
            const [rows] = await db.execute(`    SELECT 
                del.id, 
                del.pickup_address, 
                del.delivery_address, 
                del.deadline,
                del.vehicle_id,
                v.fuel_consumption,
                v.fuel_type,
                fp.price_per_liter as fuel_price
            FROM deliveries del
            JOIN drivers d ON del.driver_id = d.id
            LEFT JOIN vehicles v ON del.vehicle_id = v.id
            JOIN fuel_prices fp ON v.fuel_type = fp.fuel_type
            WHERE d.user_id = ? 
        `, [userId]);


        res.json(rows);
    } catch (err) {
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});
// get delivery details
app.get('/api/deliveries/:id', async (req, res) => {
    const deliveryId = req.params.id;
    try{
        const [rows] = await db.execute(`SELECT u.name,v.model,v.plate_number, del.pickup_address,del.delivery_address, del.assignment_date, del.deadline,
             del.estimated_time, del.estimated_cost
              FROM deliveries del JOIN drivers d ON del.driver_id = d.id JOIN vehicles v ON del.vehicle_id = v.id 
              JOIN users u ON d.user_id = u.id
              WHERE del.id = ?`, [deliveryId]);
        res.json(rows);
    }

    catch(err){
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});

//add driver
app.post('/api/drivers', async (req, res) => {
    const { name, CNP, username, password } = req.body;

    try {
        const [userResult] = await db.execute(
            'INSERT INTO users (username, password, role, name) VALUES (?, ?, ?, ?)',
            [username, password, 'driver', name]
        );

        const newUserId = userResult.insertId;

        await db.execute(
            'INSERT INTO drivers (user_id, CNP) VALUES (?, ?)',
            [newUserId, CNP]
        );

        res.status(201).json({ message: 'Driver adaugat cu succes', userId: newUserId });

    } catch (err) {
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});
// get valori fuel_type
app.get('/api/fuel-types', async (req, res) => {
    try {
        const [rows] = await db.execute(`
            SELECT COLUMN_TYPE 
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_NAME = 'vehicles' 
            AND COLUMN_NAME = 'fuel_type'
            AND TABLE_SCHEMA = 'delivery_cost_optimizer'
        `);
        
        const enumString = rows[0].COLUMN_TYPE;
        const values = enumString
            .replace("enum(", "")
            .replace(")", "")
            .split(",")
            .map(v => v.replace(/'/g, "").trim());
        
        res.json(values);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

//get fuel values for vehicle 
app.get('/api/vehicles/:id/fuel-types', async (req, res) => {
    const vehicleId = req.params.id;

    try {
        const [rows] = await db.execute(`
            SELECT fuel_type FROM delivery_cost_optimizer.vehicles WHERE id = ?
        `, [vehicleId]);

        const value = rows[0].fuel_type;
        
        
        res.json(value);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

//get fuel price
app.get('/api/fuel-price/:fuelType', async (req, res) => {
    const fuelType = req.params.fuelType;

    try {
        const [rows] = await db.execute(
            'SELECT price_per_liter FROM fuel_prices WHERE fuel_type = ?',
            [fuelType]
        );
        
        if (rows.length > 0) {
            res.json({ price: rows[0].price_per_liter });
        } else {
            res.status(404).json({ error: 'Preț combustibil negăsit' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

//add vehicle 
app.post('/api/vehicles', async (req, res) => {
      console.log(req.body);  
const { model, plate_number, capacity_kg, fuel_consumption, accidents, fuel_type } = req.body;
    try {
        const [result] = await db.execute(
            'INSERT INTO vehicles (model, plate_number, capacity_kg, fuel_consumption, accidents, fuel_type) VALUES (?, ?, ?, ?, ?, ?)',
            [model, plate_number, capacity_kg, fuel_consumption, accidents, fuel_type]
        );

        res.status(201).json({ message: 'Vehicle adaugat cu succes', vehicleId: result.insertId });
    } catch (err) {
        res.status(500).json({ error: 'Eroare server: ' + err.message });
    }
});

// add start time for delivery 
app.post('/api/deliveries/:id/start', async (req, res) => {
    const deliveryId = req.params.id;
    
    try {
        await db.execute(
            'UPDATE deliveries SET started_at = NOW() WHERE id = ?',
            [deliveryId]
        );
        res.json({ message: 'Delivery started' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

//add in delivery history
app.post('/api/deliveries/:id/finish', async (req, res) => {
    const deliveryId = req.params.id;
    const { actual_time, actual_distance, actual_cost } = req.body;
    
    try {
        // ia datele din deliveries
        const [delivery] = await db.execute(
            'SELECT * FROM deliveries WHERE id = ?',
            [deliveryId]
        );
        
        if (delivery.length === 0) {
            return res.status(404).json({ error: 'Delivery not found' });
        }
        
        const d = delivery[0];
        
        // copiază în history 
      await db.execute(
    `INSERT INTO delivery_history 
    (delivery_id, driver_id, vehicle_id, pickup_address, delivery_address, 
     assignment_date, deadline, estimated_time, estimated_cost, 
     actual_time, actual_distance, actual_cost, status) 
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'completed')`,
    [d.id, d.driver_id, d.vehicle_id, d.pickup_address, d.delivery_address, 
     d.assignment_date, d.deadline, d.estimated_time, d.estimated_cost, 
     actual_time, actual_distance, actual_cost]
);
        
        //  sterge din deliveries
        await db.execute('DELETE FROM deliveries WHERE id = ?', [deliveryId]);
        
        res.json({ message: 'Delivery completed successfully' });
    } catch (err) {
        console.error('Finish delivery error:', err);
        res.status(500).json({ error: err.message });
    }
});

//server start

const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server pornit pe portul ${PORT}`);
});