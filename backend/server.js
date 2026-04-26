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

//get orders
app.get('/api/deliveries/driver/:id', async (req, res) => {
    const userId = req.params.id;

    try {
            const [rows] = await db.execute(`
            SELECT del.id, del.pickup_address, del.delivery_address, del.deadline
            FROM deliveries del
            JOIN drivers d ON del.driver_id = d.id
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


//server start

const PORT=process.env.PORT || 3000;
app.listen(PORT, ()=>{
    console.log(`Server pornit pe portul ${PORT}`);
});